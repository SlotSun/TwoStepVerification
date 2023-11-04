package com.slot.twostepverification.ui.home

import android.net.Uri
import androidx.datastore.preferences.protobuf.Parser
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.TwoHelper
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.utils.Parse
import com.slot.twostepverification.utils.otp.GoogleAuth
import com.slot.twostepverification.utils.otp.GoogleAuthInfoException
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TwoUiState(
    var updateTime: Long = 0,
    var listItem: List<VerificationItem> = listOf()
)

class HomeViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow(TwoUiState())
    val uiState: StateFlow<TwoUiState> = _uiState.asStateFlow()


    /**
     *  获取存储的验证码库
     */
    fun getListItem() {
        viewModelScope.launch {
            val listItem = mutableListOf<VerificationItem>()
            listItem.addAll(TwoHelper.getItems())
            _uiState.update {
                it.copy(
                    listItem = listItem
                )
            }
        }
    }

    fun removeListItem(item: VerificationItem) {
        val items = mutableListOf<VerificationItem>()
        items.addAll(_uiState.value.listItem)
        items.remove(item)
        TwoHelper.updateItems(items = items)
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    listItem = items
                )
            }
        }
    }

    /**
     *  导入URl
     */
    fun importUrl(str: String):String{
        val warnStr = ""
        val uri = Uri.parse(str)
        val items = mutableListOf<VerificationItem>()
        try {
            items.addAll(GoogleAuth.parseUri(uri = uri))
            items.addAll(uiState.value.listItem)
            viewModelScope.launch {
                // 更新
                TwoHelper.updateItems(items)
                _uiState.update {
                    it.copy(
                        listItem = items
                    )
                }
            }
        } catch (e: GoogleAuthInfoException) {
            return locale("Notice_valid_data")
        }
        return "ok"

    }

    fun onRestore() {}

}