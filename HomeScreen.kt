package com.example.ioslauncher.ui.home

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Alignment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.ioslauncher.data.AppInfo
import com.example.ioslauncher.data.AppRepository
import com.example.ioslauncher.ui.components.AppleTvRgbBorder
import com.example.ioslauncher.ui.dock.Dock
import com.example.ioslauncher.ui.library.AppLibrary
import com.example.ioslauncher.ui.theme.blurIfSupported
import com.example.ioslauncher.widgets.WidgetContainer
import com.example.ioslauncher.widgets.WidgetHostManager
import com.example.ioslauncher.widgets.WidgetPickerSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Apps preferidas para el dock; si no están instaladas se rellena con las primeras disponibles.
private val DEFAULT_DOCK = listOf(
    "com.google.android.dialer",
    "com.google.android.apps.messaging",
    "com.android.chrome",
    "com.google.android.GoogleCamera"
)

@Composable
fun LauncherRoot(
    widgetHost: WidgetHostManager,
    refreshKey: Int,
    onLaunch: (String) -> Unit,
    onPickWidget: (AppWidgetProviderInfo) -> Unit
) {
    val context = LocalContext.current
    val repo = remember { AppRepository(context) }

    val apps by produceState(initialValue = emptyList<AppInfo>(), refreshKey) {
        value = withContext(Dispatchers.IO) { repo.loadApps() }
    }

    val dockPackages = remember(apps) {
        (DEFAULT_DOCK.filter { p -> apps.any { it.packageName == p } } + apps.map { it.packageName })
            .distinct()
            .take(4)
    }
    val dockApps = dockPackages.mapNotNull { p -> apps.find { it.packageName == p } }
    val gridApps = apps.filter { it.packageName !in dockPackages }

    var showLibrary by remember { mutableStateOf(false) }
    var showWidgetPicker by remember { mutableStateOf(false) }
    val providers = remember(showWidgetPicker) {
        if (showWidgetPicker) widgetHost.providers() else emptyList()
    }

    AppleTvRgbBorder(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF07070F), Color(0xFF1A0B2E))))
    ) {
        WallpaperBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            if (widgetHost.widgets.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    widgetHost.widgets.forEach { id -> WidgetContainer(widgetHost, id) }
                }
            }

            AppPager(
                apps = gridApps,
                repo = repo,
                onLaunch = onLaunch,
                onLongPress = { showWidgetPicker = true },
                onSwipeUp = { showLibrary = true },
                modifier = Modifier.weight(1f)
            )

            Dock(
                apps = dockApps,
                repo = repo,
                onLaunch = onLaunch,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        AnimatedVisibility(
            visible = showLibrary,
            enter = slideInVertically(tween(350)) { it } + fadeIn(),
            exit = slideOutVertically(tween(300)) { it } + fadeOut()
        ) {
            AppLibrary(
                apps = apps,
                repo = repo,
                onLaunch = { pkg ->
                    showLibrary = false
                    onLaunch(pkg)
                },
                onClose = { showLibrary = false }
            )
        }

        if (showWidgetPicker) {
            WidgetPickerSheet(
                providers = providers,
                onPick = { provider ->
                    showWidgetPicker = false
                    onPickWidget(provider)
                },
                onDismiss = { showWidgetPicker = false }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppPager(
    apps: List<AppInfo>,
    repo: AppRepository,
    onLaunch: (String) -> Unit,
    onLongPress: () -> Unit,
    onSwipeUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember(apps) { apps.chunked(20) }
    val pagerState = rememberPagerState(pageCount = { maxOf(pages.size, 1) })

    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() })
            }
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onVerticalDrag = { _, dragAmount -> totalDrag += dragAmount },
                    onDragEnd = { if (totalDrag < -160f) onSwipeUp() }
                )
            }
    ) { page ->
        val pageApps = pages.getOrElse(page) { emptyList() }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(5) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { col ->
                        val app = pageApps.getOrNull(row * 4 + col)
                        if (app != null) {
                            AppIcon(app, repo, onLaunch, modifier = Modifier.weight(1f))
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WallpaperBlobs() {
    Box(Modifier.fillMaxSize().blurIfSupported(90.dp)) {
        Box(
            Modifier
                .offset(x = (-80).dp, y = 120.dp)
                .size(260.dp)
                .background(Color(0xFF7B2FF7).copy(alpha = 0.6f), CircleShape)
        )
        Box(
            Modifier
                .offset(x = 180.dp, y = 420.dp)
                .size(300.dp)
                .background(Color(0xFFFF6A00).copy(alpha = 0.5f), CircleShape)
        )
        Box(
            Modifier
                .offset(x = 40.dp, y = 760.dp)
                .size(280.dp)
                .background(Color(0xFF00C8FF).copy(alpha = 0.5f), CircleShape)
        )
    }
}
