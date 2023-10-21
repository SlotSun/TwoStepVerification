package com.slot.twostepverification

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.slot.twostepverification.ui.config.ConfigScreen
import com.slot.twostepverification.ui.home.HomeScreen
import com.slot.twostepverification.ui.home.HomeViewModel

@Composable
fun TwoNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val twoNavActions = remember(navController) { TwoNavActions(navController = navController) }
    val viewModel: HomeViewModel = viewModel()
    val twoUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val statusBarColor = colorResource(R.color.theme)
    val systemUiController = rememberSystemUiController()
    DisposableEffect(lifecycleOwner) {
        systemUiController.setStatusBarColor(statusBarColor, false)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onRestore()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier.background(colorResource(R.color.background)),
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            )
        },
    ) {
        composable(TwoDestinations.MAIN_ROUTE) {
            HomeScreen(
                onNavigateToConfig = { twoNavActions.navigateToConfig() },
            )
        }
        composable(TwoDestinations.CONFIG) {
            ConfigScreen(
                onNavigateToBackup = { twoNavActions.navigateToConfig() },

                )
        }
    }
}

class TwoNavActions(
    private val navController: NavHostController
) {
    // 设置
    val navigateToConfig: () -> Unit = {
        navigate(TwoDestinations.CONFIG)
    }

    val navigateToLibDetail: () -> Unit = {
        navigate(TwoDestinations.LIB_DETAIL)
    }

    val navigateToLibs: () -> Unit = {
        navigate(TwoDestinations.LIBS)
    }

    val navigateToWebdav: () -> Unit = {
        navigate(TwoDestinations.WEBDAV)
    }
    val navigateToWebdavPath: () -> Unit = {
        navigate(TwoDestinations.WEBDAV_PATH)
    }


    private fun navigate(directions: String, arguments: String = "") {
        val options = navOptions { launchSingleTop = false }
        navController.navigate(directions + arguments, options)
    }
}

object TwoDestinations {
    const val MAIN_ROUTE = "main_route"
    const val CONFIG = "config"
    const val BACKUP = "backup"
    const val LIBS = "libs"
    const val LIB_DETAIL = "lib_detail"
    const val WEBDAV = "webdav"
    const val WEBDAV_PATH = "webdav_path"
}