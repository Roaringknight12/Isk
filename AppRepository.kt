package com.example.ioslauncher.data

import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap

class AppRepository(context: Context) {

    private val pm = context.packageManager
    private val ownPackage = context.packageName
    private val iconCache = HashMap<String, ImageBitmap>()

    fun loadApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return pm.queryIntentActivities(intent, 0)
            .map { AppInfo(it.loadLabel(pm).toString(), it.activityInfo.packageName) }
            .filter { it.packageName != ownPackage }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    @Synchronized
    fun icon(packageName: String): ImageBitmap? {
        iconCache[packageName]?.let { return it }
        val bmp = runCatching {
            pm.getApplicationIcon(packageName).toBitmap(128, 128).asImageBitmap()
        }.getOrNull() ?: return null
        iconCache[packageName] = bmp
        return bmp
    }
}
