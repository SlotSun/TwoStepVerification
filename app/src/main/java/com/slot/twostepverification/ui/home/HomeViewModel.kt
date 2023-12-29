package com.slot.twostepverification.ui.home

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.ui.code.CodeUiState
import com.slot.twostepverification.ui.code.codeUiState
import com.slot.twostepverification.utils.encoding.Base32
import com.slot.twostepverification.utils.otp.GoogleAuth
import com.slot.twostepverification.utils.otp.GoogleAuthInfoException
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TwoUiState(
    var updateTime: Long = 0,
    var listItem: List<VerificationItem> = listOf(),
    val openBottomSheet: Boolean = false,
    val openItemSettings: Boolean = false,
)

class HomeViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(TwoUiState())
    val uiState = _uiState.asStateFlow()

    // 用户选中的item
    private var selectedItem: VerificationItem? = null

    //
    fun openItemSettings(item: VerificationItem) {
        selectedItem = item
        _uiState.update {
            it.copy(
                openItemSettings = true,
                openBottomSheet = true
            )
        }
        codeUiState.update {
            it.copy(
                name = item.name!!,
                type = item.type!!,
                vindor = item.vendor!!,
                // key不能手动修改 导出的是加密过的
                key = Base32.encode(item.key!!),
                time = item.time!!,
                count = item.counter!!,
                digits = item.length!!,
                sha = item.sha!!,
                edit = false
            )
        }
    }

    fun initCodeUiState() {
        codeUiState.update {
            it.copy(
                name = "",
                vindor = "",
                key = "",
                edit = true,
            )
        }
    }

    fun closeItemSettings() {
        _uiState.update {
            it.copy(
                openItemSettings = false,
                openBottomSheet = false
            )
        }
    }

    // 打开底部弹起
    fun openBottomSheet() {
        _uiState.update {
            it.copy(
                openBottomSheet = true
            )
        }
    }

    // 关闭底部弹起
    fun closeBottomSheet() {
        _uiState.update {
            it.copy(
                openBottomSheet = false
            )
        }
    }

    /**
     *  获取存储的验证码库
     */
    fun getListItem() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    listItem = TwoHelper.getItems()
                )
            }
        }
    }

    fun removeListItem() {
        viewModelScope.launch {
            selectedItem?.let { TwoHelper.delItems(item = it) }
        }
    }

    /**
     *  导入URl
     */
    fun importUrl(str: String): String {
        val uri = Uri.parse(str)
        val items = mutableListOf<VerificationItem>()
        try {
            items.addAll(GoogleAuth.parseUri(uri = uri))
            viewModelScope.launch {
                // 更新
                TwoHelper.updateItems(items)
            }
        } catch (e: GoogleAuthInfoException) {
            return locale("Notice_valid_data")
        }
        return "ok"

    }

    /**
     * 拖拽列表项
     */
    fun dragItems(to:Int,from:Int){
        val upItems = TwoHelper.itemState.value.listItem.toMutableList().apply {
            add(to,removeAt(from))
        }
        viewModelScope.launch{
            TwoHelper.updateItems(upItems)
        }
    }

    fun onRestore() {}

}