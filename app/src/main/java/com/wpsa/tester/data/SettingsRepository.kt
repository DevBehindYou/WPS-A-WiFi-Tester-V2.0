package com.wpsa.tester.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class AppSettings(
    val wpsTimeoutSec: Int = 120,
    val autoDetectInterface: Boolean = true,
    val manualInterface: String = "wlan0",
    val supplicantMode: String = "AUTO",
    val enableVerboseLogging: Boolean = true,
    val themeMode: String = "SYSTEM",
    val keepScreenAwake: Boolean = true
)

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs = context.getSharedPreferences("wps_app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        return AppSettings(
            wpsTimeoutSec = prefs.getInt("wps_timeout_sec", 120),
            autoDetectInterface = prefs.getBoolean("auto_detect_interface", true),
            manualInterface = prefs.getString("manual_interface", "wlan0") ?: "wlan0",
            supplicantMode = prefs.getString("supplicant_mode", "AUTO") ?: "AUTO",
            enableVerboseLogging = prefs.getBoolean("verbose_logging", true),
            themeMode = prefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM",
            keepScreenAwake = prefs.getBoolean("keep_screen_awake", true)
        )
    }

    fun updateKeepScreenAwake(keep: Boolean) {
        prefs.edit().putBoolean("keep_screen_awake", keep).apply()
        _settings.value = _settings.value.copy(keepScreenAwake = keep)
    }

    fun updateWpsTimeout(seconds: Int) {
        prefs.edit().putInt("wps_timeout_sec", seconds).apply()
        _settings.value = _settings.value.copy(wpsTimeoutSec = seconds)
    }

    fun updateAutoDetectInterface(auto: Boolean) {
        prefs.edit().putBoolean("auto_detect_interface", auto).apply()
        _settings.value = _settings.value.copy(autoDetectInterface = auto)
    }

    fun updateManualInterface(iface: String) {
        prefs.edit().putString("manual_interface", iface).apply()
        _settings.value = _settings.value.copy(manualInterface = iface)
    }

    fun updateSupplicantMode(mode: String) {
        prefs.edit().putString("supplicant_mode", mode).apply()
        _settings.value = _settings.value.copy(supplicantMode = mode)
    }

    fun updateVerboseLogging(verbose: Boolean) {
        prefs.edit().putBoolean("verbose_logging", verbose).apply()
        _settings.value = _settings.value.copy(enableVerboseLogging = verbose)
    }

    fun updateThemeMode(theme: String) {
        prefs.edit().putString("theme_mode", theme).apply()
        _settings.value = _settings.value.copy(themeMode = theme)
    }
}
