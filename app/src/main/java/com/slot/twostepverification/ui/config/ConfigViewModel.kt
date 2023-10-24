package com.slot.twostepverification.ui.config

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.slot.twostepverification.R
import com.slot.twostepverification.TwoApplication
import com.slot.twostepverification.const.DYNAMIC_COLOR
import com.slot.twostepverification.const.LOCALE
import com.slot.twostepverification.ui.theme.dynamicColorState
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.utils.showToast
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConfigUIState(
    var dynamicColorChecked: Boolean = DataStoreUtils.getSyncData(DYNAMIC_COLOR, true),
    var openThemeDialog: Boolean = false,
    var openLocaleDialog: Boolean = false
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
    // 开启动态取色
    fun setDynamicColor(it: Boolean, ctx: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && it) {
            showToast(ctx, text = ctx.resources.getString(R.string.sorry_dynamic_color))
        }
        dynamicColorState.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && it
        DataStoreUtils.putSyncData(DYNAMIC_COLOR, dynamicColorState.value)
        _uiState.update { state ->
            state.copy(dynamicColorChecked = dynamicColorState.value)
        }
    }

    fun openThemeDialog() {
        _uiState.update { state ->
            state.copy(
                openThemeDialog = true
            )
        }
    }
    fun closeThemeDialog() {
        _uiState.update { state ->
            state.copy(
                openThemeDialog = false
            )
        }
    }

    fun openLocaleDialog() {
        _uiState.update { state ->
            state.copy(
                openLocaleDialog = true
            )
        }
    }
    fun closeLocaleDialog() {
        _uiState.update { state ->
            state.copy(
                openLocaleDialog = false
            )
        }
    }
    // 点击切换不重启app：微信是重绘activity：所以我决定采用i18n策略
    fun changeLocale(locale: Map<String, String>, key: String) {
        TwoApplication.localeState.value = locale
        DataStoreUtils.putSyncData(LOCALE, key)
    }

}