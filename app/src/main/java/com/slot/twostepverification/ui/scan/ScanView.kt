package com.slot.twostepverification.ui.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.camera.AspectRatioCameraConfig
import com.slot.twostepverification.utils.camera.CameraViewPermission
import com.slot.twostepverification.utils.camera.DrawCropScan

/*
*   扫码
*   开始造轮子
*
* */

@Composable
fun ScanView(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onNavigateBack: () -> Unit = {},
) {
    val ctx = LocalContext.current

    val viewModel = remember {
        val config = AspectRatioCameraConfig(ctx)
        val model = ScanViewModel(config)
        model.analyze()
        model
    }
    //UI
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()
    ) {

        // 请求权限
        CameraViewPermission(
            modifier = Modifier.fillMaxSize(),
            preview = viewModel.preview,
            imageAnalysis = viewModel.imageAnalysis,
            imageCapture = viewModel.imageCapture,
            enableTorch = viewModel.enableTorch.value
        )

        // 裁剪区域
        DrawCropScan(
            topLeftScale = Offset(x = 0.2f, y = 0.25f),
            sizeScale = Size(width = 0.6f, height = 0f)
        )


        Column(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(bottom = 25.dp, end = 25.dp)
                .padding(vertical = 15.dp)
        ) {
            Button(
                onClick = { viewModel.toggleTorch() },
                modifier = Modifier
                    .padding(10.dp)
                    .align(alignment = Alignment.End),
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp)
                ) {
                Icon(
                    imageVector = if (viewModel.enableTorch.value) {
                        Icons.Filled.FlashOff
                    } else {
                        Icons.Filled.FlashOn
                    },
                    contentDescription = "Image",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp),
                )
            }

            Button(
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3F),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {},
            ) {
                Icon(
                    Icons.Filled.Photo,
                    contentDescription = "Image",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(0.dp)
                        .size(40.dp)
                )
                Text(locale("Select_Image"))
            }
        }

    }
    if (viewModel.scanBarcodeRes.value) {
        viewModel.scanBarcodeRes.value = false
        onNavigateBack()
//            viewModel.analyzeReStart()
    }
    // 实现切换界面，重置扫码分析状态
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                // Toast.makeText(context, "start", Toast.LENGTH_SHORT).show()
                // 重新进入页面，恢复解码
                viewModel.analyzeReStart()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

}