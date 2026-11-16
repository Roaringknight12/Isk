package com.example.ioslauncher.ui.dock

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ioslauncher.data.AppInfo
import com.example.ioslauncher.data.AppRepository
import com.example.ioslauncher.ui.home.AppIcon

/** Dock flotante de 4 slots fijos con fondo translúcido. */
@Composable
fun Dock(
    apps: List<AppInfo>,
    repo: AppRepository,
    onLaunch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)
    val glassAlpha = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) 0.18f else 0.32f

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White.copy(alpha = glassAlpha))
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { i ->
            val app = apps.getOrNull(i)
            if (app != null) {
                AppIcon(app, repo, onLaunch, iconSize = 60.dp, showLabel = false)
            } else {
                Spacer(Modifier.size(60.dp))
            }
        }
    }
}
