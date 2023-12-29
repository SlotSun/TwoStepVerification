package com.slot.twostepverification.ui.scan

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.utils.camera.CameraConfig
import com.slot.twostepverification.utils.otp.GoogleAuth
import com.slot.twostepverification.utils.otp.GoogleAuthInfoException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

data class ScanUIState(
    var openWarnDialog: Boolean = false,
)

class ScanViewModel(config: CameraConfig) : ViewModel() {
    private val _scanUiState = MutableStateFlow(ScanUIState())
    val uiState: StateFlow<ScanUIState> = _scanUiState.asStateFlow()
    val preview = config.options(Preview.Builder())
    val imageCapture: ImageCapture = config.options(ImageCapture.Builder())
    val imageAnalysis: ImageAnalysis = config.options(ImageAnalysis.Builder())
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    )
    var scanBarcodeRes = mutableStateOf(false)


    // 是否开启闪光灯
    var enableTorch: MutableState<Boolean> = mutableStateOf(false)
    private var enableAnalysis = true

    // 重新识别
    fun analyzeReStart() {
        enableAnalysis = true
    }

    fun analyzeStop() {
        enableAnalysis = false
    }

    /**
     *  解析扫码结果
     */
    @SuppressLint("UnsafeOptInUsageError")
    fun analyze() {
        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { image ->
            //Log.d("enableAnalysis", "enableAnalysis: $enableAnalysis")
            if (!enableAnalysis || image.image == null) {
                image.close()
                return@setAnalyzer
            }

            enableAnalysis = false

            val mediaImage = image.image!!

            val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)

            val task = analyzeBarcode(inputImage)

            task.addOnCompleteListener {
                analyzeStop()
                image.close()
            }
        }
    }

    // 二维码识别
    fun analyzeBarcode(inputImage: InputImage,select: Boolean = false): Task<out Any> {
        return barcodeScanner.process(inputImage)
            .addOnSuccessListener {
                try {
//                    if (it.size != 0) {
                        val itemList = getScanResult(it,select)
                        if (itemList.isNotEmpty()) {
                            viewModelScope.launch {
                                scanBarcodeRes.value = true
                                TwoHelper.updateItems(items = itemList)
                            }
//                        }
                    } else {
                        analyzeReStart()
                    }

                } catch (e: Exception) {
                    Log.e("分析失败", e.message.toString())
                    _scanUiState.update {
                        it.copy(
                            openWarnDialog = true
                        )
                    }
                }
            }
            .addOnFailureListener {
                Log.d("zzz", "onFailure")
                analyzeReStart()
            }
    }

    /**
     *  关闭警告窗
     */
    fun closeWarnDialog() {
        analyzeReStart()
        _scanUiState.update { state ->
            state.copy(
                openWarnDialog = false
            )
        }
    }


    @Throws(GoogleAuthInfoException::class)
    private fun getScanResult(list: List<Barcode>,select:Boolean = false): List<VerificationItem> {
        // 设备选择
        if (list.isEmpty() && select ){
            throw Exception("")
        }
        list.forEach { barcode ->
            val code = barcode.displayValue ?: ""
            code.isNotEmpty().apply {
                val uri = Uri.parse(code)
                try {
                    return GoogleAuth.parseUri(uri = uri)
                } catch (e: GoogleAuthInfoException) {
                    throw e
                }
            }
        }
        return listOf()
    }


    /**
     *  切换闪光灯
     */
    fun toggleTorch() {
        enableTorch.value = !enableTorch.value
    }

}