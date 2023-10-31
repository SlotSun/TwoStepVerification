package com.slot.twostepverification.ui.scan

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions
import com.slot.twostepverification.data.TwoHelper
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.ui.home.TwoUiState
import com.slot.twostepverification.utils.camera.CameraConfig
import com.slot.twostepverification.utils.camera.cropTextImage
import com.slot.twostepverification.utils.otp.GoogleAuth
import com.slot.twostepverification.utils.otp.GoogleAuthInfoException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class ScanViewModel(config: CameraConfig) : ViewModel() {

    private val _uiState = MutableStateFlow(TwoUiState())
    val preview = config.options(Preview.Builder())
    val imageCapture: ImageCapture = config.options(ImageCapture.Builder())
    val imageAnalysis: ImageAnalysis = config.options(ImageAnalysis.Builder())
    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    )


    private val textRecognizer: TextRecognizer = TextRecognition.getClient(
        ChineseTextRecognizerOptions.Builder().build()
    )
    var scanText = mutableStateOf("")
    var scanBarcodeRes = mutableStateOf(false)

    // 是否开启闪光灯
    var enableTorch: MutableState<Boolean> = mutableStateOf(false)

    private var useOCR = false

    private var enableAnalysis = true


    // 重新识别
    fun analyzeReStart() {
        enableAnalysis = true
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

            val task = if (useOCR) {

                // OCR 识别, 截取扫描框图片
                val bitmap = cropTextImage(image) ?: return@setAnalyzer

                val inputImageCrop = InputImage.fromBitmap(bitmap, 0)

                textRecognizer.process(inputImageCrop)
                    .addOnSuccessListener {
                        val text = it.text

                        Log.d("zzz", "textRecognizer onSuccess")
                        Log.d("zzzzzz OCR result", "ocr result: $text")
                        scanText.value = text

                        analyzeReStart()

                    }.addOnFailureListener {
                        Log.d("zzz", "onFailure")
                        scanText.value = "onFailure"

                        analyzeReStart()
                    }

            } else {
                barcodeScanner.process(inputImage)
                    .addOnSuccessListener {
                        Log.d("zzz", "barcodeScanner onSuccess")
                        try {
                            val itemList = getScanResult(it)
                            if (itemList.isNotEmpty()) {
                                viewModelScope.launch {
                                    val items = mutableListOf<VerificationItem>()
                                    items.addAll(_uiState.value.listItem)
                                    items.addAll(itemList)
                                    scanBarcodeRes.value = true
                                    TwoHelper.updateItems(items = items)
                                    withContext(Dispatchers.IO) {
                                        _uiState.update {
                                            it.copy(
                                                listItem = items
                                            )
                                        }
                                    }
                                }
                            }else{
                                analyzeReStart()
                            }

                        }catch(e:Exception){
                            Log.e("分析失败",e.message.toString())
                            analyzeReStart()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("zzz", "onFailure")
                        analyzeReStart()
                    }
            }


            task.addOnCompleteListener {
                image.close()
            }
        }
    }

    @Throws(GoogleAuthInfoException::class)
    private fun getScanResult(list: List<Barcode>): List<VerificationItem> {
        list.forEach { barcode ->
            val code = barcode.displayValue ?: ""
            code.isNotEmpty().apply {
                val uri = Uri.parse(code)
                try {
                    return GoogleAuth.parseUri(uri = uri)
                }catch (e:GoogleAuthInfoException){
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