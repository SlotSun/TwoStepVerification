package com.slot.twostepverification.ui.home

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.utils.otp.GoogleAuth
import com.slot.twostepverification.utils.otp.GoogleAuthInfoException
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

data class TwoUiState(
    var updateTime: Long = 0,
    var listItem: List<VerificationItem> = listOf()
)

class HomeViewModel : BaseViewModel() {

    /**
     *  获取存储的验证码库
     */
    fun getListItem() {
        viewModelScope.launch {
           TwoHelper.getItems()
        }
    }

    fun removeListItem(item: VerificationItem) {
        viewModelScope.launch {
            TwoHelper.delItems(item = item)
        }
    }

    /**
     *  导入URl
     */
    fun importUrl(str: String):String{
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

    fun onRestore() {}

}