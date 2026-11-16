package com.example.ioslauncher.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.mutableStateListOf

/** Gestiona el AppWidgetHost, los ids de widgets y su persistencia. */
class WidgetHostManager(context: Context) {

    private val appContext = context.applicationContext
    private val pm = appContext.packageManager
    private val manager: AppWidgetManager = AppWidgetManager.getInstance(appContext)
    val host = AppWidgetHost(appContext, HOST_ID)
    private val prefs = appContext.getSharedPreferences("widgets", Context.MODE_PRIVATE)

    val widgets = mutableStateListOf<Int>()

    init {
        prefs.getString(KEY, "").orEmpty()
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .filter { manager.getAppWidgetInfo(it) != null }
            .forEach { widgets.add(it) }
    }

    fun providers(): List<AppWidgetProviderInfo> =
        manager.installedProviders.sortedBy { it.loadLabel(pm).toString() }

    fun info(id: Int): AppWidgetProviderInfo? = manager.getAppWidgetInfo(id)

    fun allocate(): Int = host.allocateAppWidgetId()

    fun tryBind(id: Int, provider: AppWidgetProviderInfo): Boolean =
        manager.bindAppWidgetIdIfAllowed(id, provider.provider)

    fun bindIntent(id: Int, provider: AppWidgetProviderInfo): Intent =
        Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)

    fun configureIntent(id: Int, info: AppWidgetProviderInfo): Intent? =
        info.configure?.let {
            Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
                .setComponent(it)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
        }

    fun add(id: Int) {
        if (id !in widgets) {
            widgets.add(id)
            save()
        }
    }

    fun discard(id: Int) {
        host.deleteAppWidgetId(id)
    }

    fun remove(id: Int) {
        host.deleteAppWidgetId(id)
        widgets.remove(id)
        save()
    }

    private fun save() {
        prefs.edit().putString(KEY, widgets.joinToString(",")).apply()
    }

    companion object {
        private const val HOST_ID = 1024
        private const val KEY = "ids"
    }
}
