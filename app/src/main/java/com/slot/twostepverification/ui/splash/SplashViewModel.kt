package com.slot.twostepverification.ui.splash

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.viewModelScope
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.biometric.authenticate
import com.slot.twostepverification.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {
    fun checkLoginState(
        ctx: Context,
        onError: (String, String) -> Unit,
        onSuccess:()->Unit,
        keyguardLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    ) {
        viewModelScope.launch {
            delay(500)
            if (LocalConfig.securityOpenState.value){
                authenticate(
                    title = locale("app_name"),
                    context = ctx,
                    onSuccess = {
                        onSuccess()
                    },
                    onError = onError,
                    keyguardLauncher = keyguardLauncher,
                )
            }else{
                onSuccess()
            }
        }
    }

}