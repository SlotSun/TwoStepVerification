package com.slot.twostepverification.ui.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.slot.twostepverification.const.DYNAMIC_COLOR
import com.slot.twostepverification.ui.theme.dynamicColorState
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConfigUIState(
    var dynamicColorChecked: Boolean = DataStoreUtils.getSyncData(DYNAMIC_COLOR,true),
    var openDialog:Boolean = false
)

class ConfigViewModel : BaseViewModel() {
    private val _uiState = MutableStateFlow(ConfigUIState())
    val uiState: StateFlow<ConfigUIState> = _uiState.asStateFlow()
    // 打开项目github
    fun openGithub(ctx: Context) {
        val url = Uri.parse("https://github.com/SlotSun/TwoStepVerification")
        val intent: Intent = Intent(Intent.ACTION_VIEW, url)
        ctx.startActivity(intent)
    }

    fun setDynamicColor(it: Boolean) {
        dynamicColorState.value = it
        DataStoreUtils.putSyncData(DYNAMIC_COLOR, it)
        _uiState.update { state ->
            state.copy(dynamicColorChecked = it)
        }
    }
    fun openDialog(){
        _uiState.update { state->
            state.copy(
                openDialog = true
            )
        }
    }
    fun closeDialog(){
        _uiState.update { state->
            state.copy(
                openDialog = false
            )
        }
    }


}