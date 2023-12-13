package com.slot.twostepverification

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.slot.twostepverification.ui.theme.TwoStepVerificationTheme
import com.slot.twostepverification.ui.theme.getCurrentColors


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
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