package com.slot.twostepverification.ui.splash

import android.app.Activity
import android.view.animation.OvershootInterpolator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
        SplashBgOne()
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

@ExperimentalAnimationApi
@Composable
fun SplashBgOne() {
    // 先来创建三个动画空间
    val alphaAnim = remember {
        Animatable(0f)
    }
    val cornerAnim = remember {
        Animatable(50f)
    }
    val scaleAnim = remember {
        Animatable(0.2f)
    }


    LaunchedEffect(key1 = true) {
        // 给大背景加入渐出动画
        alphaAnim.animateTo(targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(0.8f).getInterpolation(it)
                }
            )
        )
        // 给大背景加入圆角动画
        cornerAnim.animateTo(targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(0.8f).getInterpolation(it)
                }
            )
        )
        // 给大背景加入缩放动画
        scaleAnim.animateTo(targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = {
                    OvershootInterpolator(0.6f).getInterpolation(it)
                }
            )
        )
    }
    // 给图片加上属性，值来源于对应的动画值
    Image(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxSize()
            .scale(scaleAnim.value)
            .alpha(alpha = alphaAnim.value)
            .clip(shape = RoundedCornerShape(1.dp.times(cornerAnim.value.toInt()))),
        //.align(Alignment.Center),
        painter = painterResource(id = R.drawable.splash_pic),
        contentScale = ContentScale.FillBounds,
        contentDescription = null
    )
}

