package com.slot.twostepverification

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.slot.twostepverification.ui.code.CodeView
import com.slot.twostepverification.ui.config.ConfigScreen
import com.slot.twostepverification.ui.home.HomeScreen
import com.slot.twostepverification.ui.home.HomeViewModel
import com.slot.twostepverification.ui.libs.LibsDetailScreen
import com.slot.twostepverification.ui.libs.LibsScreen
import com.slot.twostepverification.ui.nav.NavScreen
import com.slot.twostepverification.ui.nav.webdav.WebDavView
import com.slot.twostepverification.ui.scan.ScanView
import com.slot.twostepverification.ui.splash.SplashScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TwoNavGraph(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val twoNavActions = remember(navController) { TwoNavActions(navController = navController) }
    val viewModel: HomeViewModel = viewModel()
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
        composable(TwoDestinations.SPLASH){
            SplashScreen(
                navNexEvent = {twoNavActions.navigateToMain()}
            )
        }
        composable(TwoDestinations.MAIN_ROUTE) {
            HomeScreen(
                onNavigateToConfig = { twoNavActions.navigateToConfig() },
                onNavigateToScan = { twoNavActions.navigateToScan() },
                onNavigateToCode = { twoNavActions.navigateToCode() }
            )
        }
        composable(TwoDestinations.CONFIG) {
            ConfigScreen(
                onNavigateToLibs = { twoNavActions.navigateToLibs() },
                onNavigateToBackup = { twoNavActions.navigateToNav() },
                onPopBackStackToMain = { twoNavActions.popBackStack(TwoDestinations.MAIN_ROUTE) }
            )
        }
        composable(TwoDestinations.LIBS) {
            LibsScreen(
                onNavigateToLibDetail = { twoNavActions.navigateToLibDetail(it) },
                onPopBackStackToConfig = { twoNavActions.popBackStack(TwoDestinations.CONFIG) }
            )
        }
        composable("${TwoDestinations.LIB_DETAIL}/{lib}") { backStackEntry ->
            LibsDetailScreen(
                lib = backStackEntry.arguments?.getString("lib") ?: "",
                onPopBackStackToLibs = { twoNavActions.popBackStack(TwoDestinations.LIBS) }
            )
        }
        composable(TwoDestinations.SCAN) {
            ScanView(
                onNavigateBack = { twoNavActions.popBackStackLast() }
            )
        }
        composable(TwoDestinations.CODE) {
            CodeView(
                onNavigateBack = { twoNavActions.popBackStackLast() }
            )
        }
        composable(TwoDestinations.NAV) {
            NavScreen(
                onNavigateToWebDav = {
                    twoNavActions.navigateToWebdav()
                },
                onPopBackStack = { twoNavActions.popBackStackLast() }
            )
        }
        composable(TwoDestinations.WEBDAV) {
            WebDavView(
                onPopBackStack = {
                    twoNavActions.popBackStackLast()
                },
                onPopBackStackToNav = {
                    twoNavActions.popBackStack(TwoDestinations.NAV)
                }
            )
        }
        composable(TwoDestinations.CODE) {
            CodeView(
                onNavigateBack = { twoNavActions.popBackStackLast() }
            )
        }

    }
}

class TwoNavActions(
    private val navController: NavHostController
) {

    val navigateToMain: () -> Unit = {
        // 先退栈
        navController.popBackStack()
        navigate(TwoDestinations.MAIN_ROUTE)
    }
    // 设置
    val navigateToConfig: () -> Unit = {
        navigate(TwoDestinations.CONFIG)
    }
    val navigateToScan: () -> Unit = {
        navigate(TwoDestinations.SCAN)
    }

    val navigateToLibDetail: (lib: String) -> Unit = {
        navigate(TwoDestinations.LIB_DETAIL, "/$it")
    }

    val navigateToLibs: () -> Unit = {
        navigate(TwoDestinations.LIBS)
    }

    // add new verification_item by user input
    val navigateToCode: () -> Unit = {
        navigate(TwoDestinations.CODE)
    }

    // 云备份
    val navigateToNav: () -> Unit = {
        navigate(TwoDestinations.NAV)
    }
    val navigateToWebdav: () -> Unit = {
        navigate(TwoDestinations.WEBDAV)
    }
    val navigateToWebdavPath: () -> Unit = {
        navigate(TwoDestinations.WEBDAV_PATH)
    }
    val popBackStack: (route: String) -> Unit = {
        navController.popBackStack(it, false)
    }
    val popBackStackLast: () -> Unit = {
        navController.popBackStack()
    }


    private fun navigate(directions: String, arguments: String = "") {
        val options = navOptions { launchSingleTop = false }
        navController.navigate(directions + arguments, options)
    }
}

object TwoDestinations {
    const val SPLASH = "splash"
    const val MAIN_ROUTE = "main_route"
    const val CONFIG = "config"
    const val BACKUP = "backup"
    const val LIBS = "libs"
    const val LIB_DETAIL = "lib_detail"
    const val WEBDAV = "webdav"
    const val WEBDAV_PATH = "webdav_path"
    const val SCAN = "scan"
    const val CODE = "code"
    const val NAV = "nav"
}