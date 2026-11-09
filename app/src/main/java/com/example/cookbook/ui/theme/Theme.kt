package com.example.cookbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = OnTerracottaPrimary,
    primaryContainer = TerracottaContainer,
    onPrimaryContainer = OnTerracottaContainer,
    secondary = OrangeSecondary,
    onSecondary = OnOrangeSecondary,
    secondaryContainer = OrangeSecondaryContainer,
    onSecondaryContainer = OnOrangeSecondaryContainer,
    background = CreamBackground,
    onBackground = OnSurfaceDark,
    surface = BeigeSurface,
    onSurface = OnSurfaceDark,
    surfaceVariant = BeigeSurfaceVariant,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = WarmOutline,
    error = CulinaryError,
    onError = OnCulinaryError
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB59D),
    onPrimary = Color(0xFF5E1700),
    primaryContainer = Color(0xFF832E0D),
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = Color(0xFFFFB68E),
    onSecondary = Color(0xFF542200),
    secondaryContainer = Color(0xFF783300),
    onSecondaryContainer = Color(0xFFFFDBCB),
    background = Color(0xFF1D1714),
    onBackground = Color(0xFFECE0DA),
    surface = Color(0xFF261E1A),
    onSurface = Color(0xFFECE0DA),
    surfaceVariant = Color(0xFF53433D),
    onSurfaceVariant = Color(0xFFD8C2BB),
    outline = Color(0xFFA08D86),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun CookBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
