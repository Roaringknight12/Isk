package com.example.ioslauncher

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableIntStateOf
import com.example.ioslauncher.ui.home.LauncherRoot
import com.example.ioslauncher.ui.theme.IosLauncherTheme
import com.example.ioslauncher.widgets.WidgetHostManager

class MainActivity : ComponentActivity() {

    private lateinit var widgetHost: WidgetHostManager
    private var pendingWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private val refreshTick = mutableIntStateOf(0)

    private val bindLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) continueWidgetSetup(pendingWidgetId)
        else widgetHost.discard(pendingWidgetId)
    }

    private val configLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) widgetHost.add(pendingWidgetId)
        else widgetHost.discard(pendingWidgetId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        widgetHost = WidgetHostManager(this)
        setContent {
            IosLauncherTheme {
                LauncherRoot(
                    widgetHost = widgetHost,
                    refreshKey = refreshTick.intValue,
                    onLaunch = ::launchApp,
                    onPickWidget = ::startWidgetFlow
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        widgetHost.host.startListening()
    }

    override fun onStop() {
        super.onStop()
        widgetHost.host.stopListening()
    }

    override fun onResume() {
        super.onResume()
        refreshTick.intValue++
    }

    private fun launchApp(packageName: String) {
        packageManager.getLaunchIntentForPackage(packageName)?.let { startActivity(it) }
    }

    private fun startWidgetFlow(provider: AppWidgetProviderInfo) {
        val id = widgetHost.allocate()
        pendingWidgetId = id
        if (widgetHost.tryBind(id, provider)) continueWidgetSetup(id)
        else bindLauncher.launch(widgetHost.bindIntent(id, provider))
    }

    private fun continueWidgetSetup(id: Int) {
        val info = widgetHost.info(id)
        if (info == null) {
            widgetHost.discard(id)
            return
        }
        val configIntent = widgetHost.configureIntent(id, info)
        if (configIntent != null) configLauncher.launch(configIntent) else widgetHost.add(id)
    }
}
