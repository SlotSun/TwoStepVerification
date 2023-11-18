package com.slot.twostepverification.ui.libs

import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.utils.https.get
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class libDetailUiState(
    var isLoading: Boolean = true,
    var success: Boolean = false,
    var message: String = "",
)

class LibDetailViewModel: BaseViewModel() {
    private val _uiState = MutableStateFlow(libDetailUiState())
    val uiState: StateFlow<libDetailUiState> = _uiState.asStateFlow()
    val lisRegex = Regex("(?<=<pre>)([\\s\\S]*?)(?=<\\/pre>)")
    // 获取license
    fun getLicenseString(lib:String){
        //正在加载
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            // 单例模式
            val response = get("https://pub.dev/packages/$lib/license")
            var msg = lisRegex.find(response)?.value
            if (msg == null){
                msg = "发生了某种错误"
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    message = "$msg"
                )
            }
        }

    }

    // 组件结束时候，取消协程内的任务
    override fun onCleared() {
        viewModelScope.cancel()
    }

}