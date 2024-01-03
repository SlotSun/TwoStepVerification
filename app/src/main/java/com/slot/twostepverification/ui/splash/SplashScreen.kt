package com.slot.twostepverification.ui.splash

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.R
import com.slot.twostepverification.utils.showToasts

/**
 * 启动页入口
 * @param navNexEvent 下一步动作回调方法，跳转登录或者主页
 */
@ExperimentalAnimationApi
@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = viewModel(),
    navNexEvent: () -> Unit
) {
    val ctx = LocalContext.current
    val keyguardLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // todo : xxxx
            }
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // 大背景图片
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            painter = painterResource(id = R.drawable.splash_pic),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )
    }
    LaunchedEffect(Unit){
        splashViewModel.checkLoginState(
            ctx = ctx,
            onError = { title, message ->
                ctx.showToasts(message)
            },
            onSuccess = {
                navNexEvent()
            },
            keyguardLauncher = keyguardLauncher
        )
    }
}

