package com.slot.twostepverification.ui.code

import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.data.entity.VerificationItem
import com.slot.twostepverification.utils.encoding.Base32
import com.slot.twostepverification.utils.showToasts
import com.slot.twostepverification.utils.widget.TextFieldController
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import splitties.init.appCtx


data class CodeUiState(
    var type:String ="TOTP",
    var name: String = "",
    var vindor: String = "",
    var key: String = "",
    var time: Int = 30,
    var count: Int = 0,
    var digits: Int = 6,
    var sha: String = "SHA1",
    val edit:Boolean = true,
)
val codeUiState = MutableStateFlow(CodeUiState())
class CodeViewModel : BaseViewModel() {
    private val nameControllerState = MutableStateFlow(
        TextFieldController(
            isError = false,
            supportingText = "please input website account",
            errorMessage = "account can't be null"
        )
    )
    private val vindorControllerState = MutableStateFlow(
        TextFieldController(
            isError = false,
            supportingText = "please input vendor",
            errorMessage = "vendor can't be null"
        )
    )
    private val keyControllerState = MutableStateFlow(
        TextFieldController(
            isError = false,
            supportingText = "please input key",
            errorMessage = "key can't be null"
        )
    )
    private val timeControllerState = MutableStateFlow(
        TextFieldController(
            isError = false,
            supportingText = "please input time",
            errorMessage = "time can't be null"
        )
    )
    val nameTextFieldController: StateFlow<TextFieldController> = nameControllerState.asStateFlow()
    val vindorTextFieldController: StateFlow<TextFieldController> = vindorControllerState.asStateFlow()
    val keyTextFieldController: StateFlow<TextFieldController> = keyControllerState.asStateFlow()
    val timeTextFieldController: StateFlow<TextFieldController> = timeControllerState.asStateFlow()

    val uiState: StateFlow<CodeUiState> = codeUiState.asStateFlow()


   private fun updateItem() {
        val item = VerificationItem(
            type = uiState.value.type,
            name = uiState.value.name,
            vendor = uiState.value.vindor,
            key = Base32.decode(uiState.value.key),
            time = uiState.value.time,
            length = uiState.value.digits,
            counter = uiState.value.count,
            sha = uiState.value.sha
        )

        viewModelScope.launch {
            val items = listOf(item)
            TwoHelper.updateItems(items)
        }
    }
    // 提交
    fun submit():Boolean{
        var res = verify()
        if(res){
            updateItem()
        }
        return res
    }

    private fun verify():Boolean {
        var valid = false
        viewModelScope.launch {
            nameControllerState.update {
                val isError = uiState.value.name == ""
                valid = !isError
                it.copy(
                    isError = isError
                )
            }
            vindorControllerState.update {
                val isError = uiState.value.vindor == ""
                it.copy(
                    isError = isError
                )
            }
            keyControllerState.update {
                var errorStr = "key can't be null"
                val isError = if (uiState.value.name.isEmpty()) true else {
                    try {
                        Base32.decode(uiState.value.key)
                        false
                    } catch (e: Exception) {
                        errorStr = "Access Key is not a valid Base32 encoding"
                        true
                    }
                }
                valid = !isError
                it.copy(
                    isError = isError,
                    errorMessage = errorStr
                )
            }
        }
        return valid
    }

}