package group.project.fixitapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = OrangeDark,     // Darker orange for primary elements in dark theme
    onPrimary = White,        // White text/icons on dark orange background
    secondary = Orange,// Standard orange for secondary elements
    onSecondary = Black,      // Black text/icons on standard orange background
    tertiary = BlueDark,          // Blue for cancel buttons
    background = Black,       // Black background
    onBackground = White,     // White text/icons on black background
    surface = OrangeLight,    // Lighter orange for surfaces
    onSurface = Black,         // Black text/icons on light orange surface
    onTertiary = GreyDark, //Color for dividers
    error = RedDark,
    onError = White,
    // ... additional colors as needed
)

private val LightColorScheme = lightColorScheme(
    primary = Orange, // Standard orange for primary elements in light theme
    onPrimary = Black,        // Black text/icons on standard orange background
    secondary = OrangeDark,   // Darker orange for secondary elements
    onSecondary = White,      // White text/icons on dark orange background
    tertiary = Blue,          // Blue for cancel buttons
    background = White,       // White background
    onBackground = Black,     // Black text/icons on white background
    surface = OrangeLight,    // Lighter orange for surfaces
    onSurface = Black,         // Black text/icons on light orange surface
    onTertiary = Grey,      //Color for dividers
    error = Red,
    onError = White
    // ... additional colors as needed
)

@Composable
private fun getCurrentColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
    val context = LocalContext.current
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
}

@Composable
fun FixItAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getCurrentColorScheme(darkTheme, dynamicColor)
    ApplySystemBarsColor(colorScheme, darkTheme)
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Suppress("DEPRECATION")
@Composable
private fun ApplySystemBarsColor(colorScheme: ColorScheme, darkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
}