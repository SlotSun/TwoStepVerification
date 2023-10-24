package com.slot.twostepverification

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.slot.twostepverification.ui.theme.TwoStepVerificationTheme
import com.slot.twostepverification.ui.theme.getCurrentColors
import com.slot.twostepverification.utils.setAndroidNativeLightStatusBar
import com.slot.twostepverification.utils.transparentStatusBar


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ComposeView(this).apply {
            setContent {
                val colors = getCurrentColors()
                TwoStepVerificationTheme(
                    colorScheme = colors,
                    dynamicColor = false
                ) {
                    val navController = rememberNavController()
                    val startDestination = TwoDestinations.MAIN_ROUTE
                    TwoNavGraph(navController, startDestination)
                }
            }
        })
    }



}