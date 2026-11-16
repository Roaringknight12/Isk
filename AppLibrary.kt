package com.example.ioslauncher.ui.library

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ioslauncher.data.AppInfo
import com.example.ioslauncher.data.AppRepository
import com.example.ioslauncher.ui.home.AppIcon

/** Biblioteca de apps: se abre deslizando hacia arriba desde la pantalla de inicio. */
@Composable
fun AppLibrary(
    apps: List<AppInfo>,
    repo: AppRepository,
    onLaunch: (String) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(apps, query) {
        apps.filter { it.label.contains(query, ignoreCase = true) }
    }

    BackHandler(onBack = onClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.78f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Biblioteca de apps",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Buscar") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(filtered, key = { it.packageName }) { app ->
                AppIcon(
                    app = app,
                    repo = repo,
                    onLaunch = onLaunch,
                    modifier = Modifier.fillMaxWidth(),
                    iconSize = 60.dp
                )
            }
        }
    }
}
