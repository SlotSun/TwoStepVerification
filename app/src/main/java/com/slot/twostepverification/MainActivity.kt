package com.slot.twostepverification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.rememberNavController
import com.slot.twostepverification.ui.theme.TwoStepVerificationTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ComposeView(this).apply {
            setContent {
                TwoStepVerificationTheme(
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