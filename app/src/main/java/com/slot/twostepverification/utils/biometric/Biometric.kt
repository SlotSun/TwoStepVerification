package com.slot.twostepverification.utils.biometric

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.getFragmentActivity

/**
 *  安全认证
 *  @see <a href="https://developer.android.com/training/sign-in/biometric-auth?hl=zh-cn"> 官方文档
 */
fun authenticate(
    title: String,
    context: Context,
    keyguardLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onError: (String, String) -> Unit,
    onSuccess:()->Unit
) {
    val fragmentContext = context.getFragmentActivity()!!
    val keyguardManager =
        fragmentContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    // Android 10（API 级别 29）及更低版本不支持以下身份验证器类型组合:DEVICE_CREDENTIAL 和 BIOMETRIC_STRONG | DEVICE_CREDENTIAL
    if (!keyguardManager.isDeviceSecure) {
        return
    }

    @Suppress("DEPRECATION")
    fun keyguardPrompt() {
        val intent = keyguardManager.createConfirmDeviceCredentialIntent(
            locale("app_name"),
            title
        )
        keyguardLauncher.launch(intent)
    }

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
        keyguardPrompt()
        return
    }

    val biometricManager = BiometricManager.from(context)
    val authenticators =
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle(locale("app_name"))
        .setSubtitle(title)
        .setAllowedAuthenticators(authenticators)
        .build()

    val biometricPrompt = BiometricPrompt(
        fragmentContext,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> keyguardPrompt()
                    BiometricPrompt.ERROR_LOCKOUT -> keyguardPrompt()
                    else ->onError(
                        errorCode.toString(),
                        errString.toString()
                    )
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError(
                    "",
                    "认证失败"
                )
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }
        }
    )

    when (biometricManager.canAuthenticate(authenticators)) {
        BiometricManager.BIOMETRIC_SUCCESS -> biometricPrompt.authenticate(promptInfo)
        else -> keyguardPrompt()
    }
}
