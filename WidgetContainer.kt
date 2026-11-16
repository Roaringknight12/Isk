package com.example.ioslauncher.widgets

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

/** Contenedor squircle redondeado que aloja un widget nativo. Mantén pulsado para quitarlo. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WidgetContainer(widgetHost: WidgetHostManager, appWidgetId: Int) {
    val info = widgetHost.info(appWidgetId) ?: return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxOf(info.minHeight, 110).dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .combinedClickable(
                onClick = {},
                onLongClick = { widgetHost.remove(appWidgetId) }
            )
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx -> widgetHost.host.createView(ctx, appWidgetId, info) }
        )
    }
}

/** Hoja inferior con los widgets instalados. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerSheet(
    providers: List<AppWidgetProviderInfo>,
    onPick: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val pm = LocalContext.current.packageManager
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1E)
    ) {
        LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            item {
                Text(
                    text = "Añadir widget",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(providers, key = { it.provider.flattenToString() }) { provider ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPick(provider) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = provider.loadLabel(pm).toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = provider.provider.packageName,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
