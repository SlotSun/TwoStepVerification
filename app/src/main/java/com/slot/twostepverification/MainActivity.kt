package com.slot.twostepverification

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.slot.twostepverification.ui.theme.TwoStepVerificationTheme
import com.slot.twostepverification.ui.theme.getCurrentColors


class MainActivity : AppCompatActivity() {

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
//                    val startDestination = TwoDestinations.MAIN_ROUTE
                    val startDestination = TwoDestinations.SPLASH
                    TwoNavGraph(navController, startDestination)
                }
            }
        })
    }
}