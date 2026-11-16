package com.example.ioslauncher.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp

@Composable
fun IosLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(), content = content)
}

// Blur real solo en Android 12+ (API 31). En versiones anteriores no hace nada.
fun Modifier.blurIfSupported(radius: Dp): Modifier =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) this.blur(radius) else this
