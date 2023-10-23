package com.slot.twostepverification.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.slot.twostepverification.const.DYNAMIC_COLOR
import com.slot.twostepverification.utils.data.DataStoreUtils

const val DYNAMIC_COLOR_SCHEME = "dynamicLightColorScheme"

// 天蓝色
const val SKY_BLUE_THEME = 0

// 灰色
const val GRAY_THEME = 1

// 绿色
const val GREEN_THEME = 3

// 紫色
const val PURPLE_THEME = 4

// 橘黄色
const val ORANGE_THEME = 5

// 青色
const val CYAN_THEME = 8

// 品红色
const val MAGENTA_THEME = 9

/**
 * 主题状态
 */
val themeTypeState: MutableState<Int> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    mutableIntStateOf(getDefaultThemeId())
}
val dynamicColorState: MutableState<Boolean> by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
    mutableStateOf(DataStoreUtils.getSyncData(DYNAMIC_COLOR,true))
}


fun getDefaultThemeId(): Int {
    return 4
}

@Composable
fun TwoStepVerificationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
    MaterialTheme(
        colorScheme= colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * 通过主题 ID 来获取需要的主题
 */
fun getThemeForThemeId(themeId: Int) = when (themeId) {
    SKY_BLUE_THEME -> {
        playLightColors(
            primary = primaryLight
        )
    }

    GRAY_THEME -> {
        playLightColors(
            primary = gray_theme
        )
    }

    GREEN_THEME -> {
        playLightColors(
            primary = green_theme
        )
    }

    PURPLE_THEME -> {
        playLightColors(
            primary = purple_theme,
        )
    }

    ORANGE_THEME -> {
        playLightColors(
            primary = orange_theme
        )
    }

    CYAN_THEME -> {
        playLightColors(
            primary = cyan_theme
        )
    }

    MAGENTA_THEME -> {
        playLightColors(
            primary = magenta_theme
        )
    }

    else -> {
        playLightColors(
            primary = primaryLight
        )
    }
}