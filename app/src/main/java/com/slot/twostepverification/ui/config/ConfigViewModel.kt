package com.slot.twostepverification.ui.config

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.slot.twostepverification.const.DYNAMIC_COLOR
import com.slot.twostepverification.const.LOCALE
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.LocalConfig.localeState
import com.slot.twostepverification.const.SECURITY_OPEN
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.biometric.authenticate
import com.slot.twostepverification.utils.data.DataStoreUtils
import com.slot.twostepverification.utils.getFragmentActivity
import com.slot.twostepverification.utils.showToast
import com.slot.twostepverification.utils.showToasts
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ConfigUIState(
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
            showToast(ctx, text = locale("sorry_dynamic_color"))
        }
        LocalConfig.dynamicColorState.value = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && it
        DataStoreUtils.putSyncData(DYNAMIC_COLOR, LocalConfig.dynamicColorState.value)
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
        localeState.value = locale
        DataStoreUtils.putSyncData(LOCALE, key)
    }


    fun changeSecurityOpen(
        title: String,
        context: Context,
        keyguardLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onError: (String, String) -> Unit,
    ) {
        if (LocalConfig.securityOpenState.value) {
            LocalConfig.securityOpenState.value = !LocalConfig.securityOpenState.value
            DataStoreUtils.putSyncData(SECURITY_OPEN, false)
        } else {
            authenticate(
                title = title,
                context = context,
                keyguardLauncher = keyguardLauncher,
                onError = onError,
                onSuccess = {
                    LocalConfig.securityOpenState.value = !LocalConfig.securityOpenState.value
                    DataStoreUtils.putSyncData(SECURITY_OPEN, true)
                    context.showToasts("开启指纹验证成功")
                }
            )
        }

    }
}