package com.slot.twostepverification.ui.scan

import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.common.InputImage
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.camera.AspectRatioCameraConfig
import com.slot.twostepverification.utils.camera.CameraViewPermission
import com.slot.twostepverification.utils.camera.DrawCropScan
import com.slot.twostepverification.utils.img.coil.CoilImageEngine
import github.leavesczy.matisse.DefaultMediaFilter
import github.leavesczy.matisse.Matisse
import github.leavesczy.matisse.MatisseContract
import github.leavesczy.matisse.MediaResource
import github.leavesczy.matisse.MimeType
import github.leavesczy.matisse.SmartCaptureStrategy
import java.io.IOException

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
    val scanUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // select_picture
    val mediaPickerLauncher =
        rememberLauncherForActivityResult(contract = MatisseContract()) { result: List<MediaResource>? ->
            if (!result.isNullOrEmpty()) {
                val mediaResource = result[0]
                val uri = mediaResource.uri
                val inputImage: InputImage
                try {
                    inputImage = InputImage.fromFilePath(ctx, uri)
                    // 图片源自用户选择：需要提示图片错误
                    viewModel.analyzeBarcode(inputImage = inputImage, select = true)
                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }
        }

    val matisse = Matisse(
        maxSelectable = 1,
        mediaFilter = DefaultMediaFilter(supportedMimeTypes = MimeType.ofImage()),
        imageEngine = CoilImageEngine(),
    )


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
            // 闪光灯
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
                        Icons.Filled.FlashOn
                    } else {
                        Icons.Filled.FlashOff
                    },
                    contentDescription = "Image",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp),
                )
            }
            // 选取图片
            Button(
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3F),
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    mediaPickerLauncher.launch(matisse)
                },
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
    // 弹出警告
    if (scanUiState.openWarnDialog) {
        AlertDialog(
            title = {
                Text(locale("ERROR"))
            },
            text = {
                Text(locale("TheQRcodeisinvalid"))
            },
            onDismissRequest = {
                viewModel.closeWarnDialog()
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.closeWarnDialog()
                }) {
                    Text(
                        text = locale("OK"),
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
                    )
                }
            }
        )
    }
    // 扫描到结果，返回主页面
    if (viewModel.scanBarcodeRes.value) {
        viewModel.scanBarcodeRes.value = false
        onNavigateBack()
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