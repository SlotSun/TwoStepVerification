package com.slot.twostepverification.ui.home.components

import android.util.Log
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.const.VIEW_CLICK_INTERVAL_TIME
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.ui.home.HomeViewModel
import com.slot.twostepverification.utils.otp.TotpInfo
import com.slot.twostepverification.utils.showToasts
import splitties.init.appCtx


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItemView(item: VerificationItem, viewModel: HomeViewModel = viewModel()) {
    val clipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current
    var preValue = 0.001f
    var beginTime by remember {
        mutableFloatStateOf(
            (1 - TotpInfo.getMillisTillNextRotation(30).toFloat() / (30 * 1000L))
        )
    }
    val transition = rememberInfiniteTransition(label = "")
    var lastTime by remember { mutableIntStateOf(TotpInfo.getMillisTillNextRotation(30).toInt()) }
    val process = transition.animateFloat(
        initialValue = beginTime, targetValue = 1.0f,
        animationSpec = InfiniteRepeatableSpec(
            tween(durationMillis = lastTime, easing = LinearEasing)
        ),
        label = "item",
    )
    var token by remember { mutableStateOf("") }
    val algorithm = item.sha
    val digits = item.length!!
    val period = item.time!!
    val secret = item.key!!
    fun getToken(){
        token = try {
            TotpInfo(secret, algorithm, digits, period).getOtp()
        } catch (e: Exception) {
            e.toString()
        }
    }
    // 获取当前组件的生命周期
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver{
            override fun onResume(owner: LifecycleOwner) {
               getToken()
            }
        }
        // 添加生命周期观察者
        lifecycleOwner.lifecycle.addObserver(observer)
        // 在 DisposableEffect 释放时移除生命周期观察者
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        getToken()
    }

    // 每30s获取一次
    SideEffect {
        beginTime = (1 - (TotpInfo.getMillisTillNextRotation(30).toFloat()) / (30 * 1000L))
        //动画不能为0
        lastTime = (beginTime * 30 * 1000L + 1).toInt()
        if (process.value <= preValue && item.type == "TOTP") {
           getToken()
            //记录当前刷新时间 第二次调用直接从0开始 ：process从0->1递增
            Log.d("got", preValue.toString())
        }

    }
    Row(
        modifier = Modifier
            .padding(
                vertical = 18.dp,
                horizontal = 18.dp
            )
            .combinedClickable(
                onClick = composeClick {
                    clipboardManager.setText(AnnotatedString(token))
                    ctx.showToasts(locale("Copied"))
                },
                onLongClick = {
                    viewModel.openItemSettings(item = item)
                },
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(fraction = 0.85f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = item.name!!,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W500
                )
            )
            Text(
                text = token,
                style = TextStyle(
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.W500
                )
            )
        }
        Box(
            modifier = Modifier
                .padding(end = 6.dp)
                .width(45.dp)
                .height(45.dp)
        ) {
            CircularProgress(process = process.value)
        }
    }
}


@Composable
fun CircularProgress(process: Float) {
    CircularProgressIndicator(
        progress = process,
        color = MaterialTheme.colorScheme.surfaceVariant,
        trackColor = MaterialTheme.colorScheme.primary,
    )
}

@Composable
inline fun composeClick(
    time: Int = VIEW_CLICK_INTERVAL_TIME,
    crossinline onClick: () -> Unit
): () -> Unit {
    var lastClickTime by remember { mutableStateOf(value = 0L) }//使用remember函数记录上次点击的时间
    return {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time >= lastClickTime) {//判断点击间隔,如果在间隔内则不回调
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}

