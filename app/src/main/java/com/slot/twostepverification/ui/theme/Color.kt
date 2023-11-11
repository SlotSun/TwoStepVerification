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
val green_theme = Color(0xFFc4e67e)

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

// 绿色主题
fun greenTheme():ColorScheme{
    val md_theme_light_primary = Color(0xFF4C6708)
    val md_theme_light_onPrimary = Color(0xFFFFFFFF)
    val md_theme_light_primaryContainer = Color(0xFFCCEF85)
    val md_theme_light_onPrimaryContainer = Color(0xFF141F00)
    val md_theme_light_secondary = Color(0xFF5A6148)
    val md_theme_light_onSecondary = Color(0xFFFFFFFF)
    val md_theme_light_secondaryContainer = Color(0xFFDEE6C5)
    val md_theme_light_onSecondaryContainer = Color(0xFF171E09)
    val md_theme_light_tertiary = Color(0xFF396660)
    val md_theme_light_onTertiary = Color(0xFFFFFFFF)
    val md_theme_light_tertiaryContainer = Color(0xFFBCECE4)
    val md_theme_light_onTertiaryContainer = Color(0xFF00201D)
    val md_theme_light_error = Color(0xFFBA1A1A)
    val md_theme_light_errorContainer = Color(0xFFFFDAD6)
    val md_theme_light_onError = Color(0xFFFFFFFF)
    val md_theme_light_onErrorContainer = Color(0xFF410002)
    val md_theme_light_background = Color(0xFFFEFCF4)
    val md_theme_light_onBackground = Color(0xFF1B1C17)
    val md_theme_light_surface = Color(0xFFFEFCF4)
    val md_theme_light_onSurface = Color(0xFF1B1C17)
    val md_theme_light_surfaceVariant = Color(0xFFE2E4D4)
    val md_theme_light_onSurfaceVariant = Color(0xFF45483C)
    val md_theme_light_outline = Color(0xFF76786B)
    val md_theme_light_inverseOnSurface = Color(0xFFF2F1E9)
    val md_theme_light_inverseSurface = Color(0xFF30312C)
    val md_theme_light_inversePrimary = Color(0xFFB1D26C)
    val md_theme_light_shadow = Color(0xFF000000)
    val md_theme_light_surfaceTint = Color(0xFF4C6708)
    val md_theme_light_outlineVariant = Color(0xFFC6C8B9)
    val md_theme_light_scrim = Color(0xFF000000)
    val seed = Color(0xFFC4E67E)
    return lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        errorContainer = md_theme_light_errorContainer,
        onError = md_theme_light_onError,
        onErrorContainer = md_theme_light_onErrorContainer,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        outline = md_theme_light_outline,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inverseSurface = md_theme_light_inverseSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )
}

fun purpleTheme():ColorScheme {
    val md_theme_light_primary = Color(0xFF6750A4)
    val md_theme_light_onPrimary = Color(0xFFFFFFFF)
    val md_theme_light_primaryContainer = Color(0xFFEADDFF)
    val md_theme_light_onPrimaryContainer = Color(0xFF21005D)
    val md_theme_light_secondary = Color(0xFF625B71)
    val md_theme_light_onSecondary = Color(0xFFFFFFFF)
    val md_theme_light_secondaryContainer = Color(0xFFE8DEF8)
    val md_theme_light_onSecondaryContainer = Color(0xFF1D192B)
    val md_theme_light_tertiary = Color(0xFF7D5260)
    val md_theme_light_onTertiary = Color(0xFFFFFFFF)
    val md_theme_light_tertiaryContainer = Color(0xFFFFD8E4)
    val md_theme_light_onTertiaryContainer = Color(0xFF31111D)
    val md_theme_light_error = Color(0xFFB3261E)
    val md_theme_light_onError = Color(0xFFFFFFFF)
    val md_theme_light_errorContainer = Color(0xFFF9DEDC)
    val md_theme_light_onErrorContainer = Color(0xFF410E0B)
    val md_theme_light_outline = Color(0xFF79747E)
    val md_theme_light_background = Color(0xFFFFFBFE)
    val md_theme_light_onBackground = Color(0xFF1C1B1F)
    val md_theme_light_surface = Color(0xFFFFFBFE)
    val md_theme_light_onSurface = Color(0xFF1C1B1F)
    val md_theme_light_surfaceVariant = Color(0xFFE7E0EC)
    val md_theme_light_onSurfaceVariant = Color(0xFF49454F)
    val md_theme_light_inverseSurface = Color(0xFF313033)
    val md_theme_light_inverseOnSurface = Color(0xFFF4EFF4)
    val md_theme_light_inversePrimary = Color(0xFFD0BCFF)
    val md_theme_light_shadow = Color(0xFF000000)
    val md_theme_light_surfaceTint = Color(0xFF6750A4)
    val md_theme_light_outlineVariant = Color(0xFFCAC4D0)
    val md_theme_light_scrim = Color(0xFF000000)
    return lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        onPrimaryContainer = md_theme_light_onPrimaryContainer,
        secondary = md_theme_light_secondary,
        onSecondary = md_theme_light_onSecondary,
        secondaryContainer = md_theme_light_secondaryContainer,
        onSecondaryContainer = md_theme_light_onSecondaryContainer,
        tertiary = md_theme_light_tertiary,
        onTertiary = md_theme_light_onTertiary,
        tertiaryContainer = md_theme_light_tertiaryContainer,
        onTertiaryContainer = md_theme_light_onTertiaryContainer,
        error = md_theme_light_error,
        onError = md_theme_light_onError,
        errorContainer = md_theme_light_errorContainer,
        onErrorContainer = md_theme_light_onErrorContainer,
        outline = md_theme_light_outline,
        background = md_theme_light_background,
        onBackground = md_theme_light_onBackground,
        surface = md_theme_light_surface,
        onSurface = md_theme_light_onSurface,
        surfaceVariant = md_theme_light_surfaceVariant,
        onSurfaceVariant = md_theme_light_onSurfaceVariant,
        inverseSurface = md_theme_light_inverseSurface,
        inverseOnSurface = md_theme_light_inverseOnSurface,
        inversePrimary = md_theme_light_inversePrimary,
        surfaceTint = md_theme_light_surfaceTint,
        outlineVariant = md_theme_light_outlineVariant,
        scrim = md_theme_light_scrim,
    )
}




/**
 *  浅色主题方法
 */
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