package com.slot.twostepverification.ui.theme

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.slot.twostepverification.TwoApplication

val white = Color.White

// 天蓝色
val select_theme = Color(0xFF65A2FF)

// 灰色
val gray_theme = Color(0xFF888888)

// 绿色
val green_theme = Color(0xFF00FF00)

// 紫色
val purple_theme = Color(0xFFD0BCFF)

// 橘黄色
val orange_theme = Color(0xFFFFA500)

// 青色
val cyan_theme = Color(0xFF00FFFF)

// 品红色
val magenta_theme = Color(0xFFFF00FF)


// Light 主题颜色
val pageLight = Color(0xFFD0BCFF)


// 浅色 主题颜色
val primaryLight = Color(0xFF85B4FC)
val primaryVariantLight = Color(0xFF7D5260)
val secondaryLight = Color(0xFF3f2c2c)
val backgroundLight = pageLight
val surfaceLight = pageLight
val onPrimaryLight = Color(0xFF232323)
val onSecondaryLight = Color(0xFFD8D7D7)
val onBackgroundLight = Color(0xFF232325)
val onSurfaceLight = Color(0xFF232323)


// Dark 主题颜色
val pageDark = Color(0xFF000000)
val primaryDark = pageDark
val primaryVariantDark = Color(0xFF3700B3)
val secondaryDark = Color(0xFFE0E0F0)
val backgroundDark = Color(0xFF1B1B1B)
val surfaceDark = Color(0xFF232323)
val onPrimaryDark = white
val onSecondaryDark = Color(0xFF3A3A3A)
val onBackgroundDark = white
val onSurfaceDark = white

@Composable
fun getCurrentColors(): ColorScheme {
    val ctx = LocalContext.current
    val themeType = themeTypeState.value
    val dy = dynamicColorState.value
    val colorScheme = if (dy && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ) {
        dynamicLightColorScheme(context = ctx)
    } else {
        //如果系统小于安卓12 关闭动态取色
        dynamicColorState.value = false
        if (isSystemInDarkTheme()) {
            playDarkColors()
        } else {
            getThemeForThemeId(themeType)
        }
    }
    return colorScheme
}

/**
 *  浅色主题方法
 */
fun playLightColors(
    primary: Color = primaryLight,
    primaryVariant: Color = primaryVariantLight,
    secondary: Color = secondaryLight,
    secondaryVariant: Color = Color(0xFF018786),
    background: Color = backgroundLight,
    surface: Color = surfaceLight,
    error: Color = Color(0xFFB00020),
    onPrimary: Color = onPrimaryLight,
    onSecondary: Color = onSecondaryLight,
    onBackground: Color = onBackgroundLight,
    onSurface: Color = onSurfaceLight,
    onError: Color = Color.White
): ColorScheme = lightColorScheme(
    primary,
    primaryVariant,
    secondary,
    secondaryVariant,
    background,
    surface,
    error,
    onPrimary,
    onSecondary,
    onBackground,
    onSurface,
    onError,
)

fun playDarkColors(
    primary: Color = primaryDark,
    primaryVariant: Color = primaryVariantDark,
    secondary: Color = secondaryDark,
    secondaryVariant: Color = secondary,
    background: Color = backgroundDark,
    surface: Color = surfaceDark,
    error: Color = Color(0xFFCF6679),
    onPrimary: Color = onPrimaryDark,
    onSecondary: Color = onSecondaryDark,
    onBackground: Color = onBackgroundDark,
    onSurface: Color = onSurfaceDark,
    onError: Color = Color.Black
): ColorScheme = darkColorScheme(
    primary,
    primaryVariant,
    secondary,
    secondaryVariant,
    background,
    surface,
    error,
    onPrimary,
    onSecondary,
    onBackground,
    onSurface,
    onError,
)