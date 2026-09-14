package com.wpsa.tester

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import com.wpsa.tester.core.logging.AppLogger
import com.wpsa.tester.data.SettingsRepository
import com.wpsa.tester.diagnostics.DeviceDiagnostics
import com.wpsa.tester.ui.screens.DiagnosticsScreen
import com.wpsa.tester.ui.screens.HomeScreen
import com.wpsa.tester.ui.screens.LogsScreen
import com.wpsa.tester.ui.screens.SettingsScreen
import com.wpsa.tester.ui.theme.WpsTheme
import javax.inject.Inject

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Networks : Screen("networks", "Networks", Icons.Default.Wifi)
    data object Diagnostics : Screen("diagnostics", "Diagnostics", Icons.Default.Sensors)
    data object Logs : Screen("logs", "Logs", Icons.AutoMirrored.Filled.Article)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var deviceDiagnostics: DeviceDiagnostics

    @Inject
    lateinit var appLogger: AppLogger

    @Inject
    lateinit var settingsRepository: SettingsRepository

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val grantedCount = permissions.values.count { it }
        appLogger.log(
            component = "MainActivity",
            operation = "permissions_result",
            result = "Granted $grantedCount of ${permissions.size} permissions"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestRequiredPermissions()

        setContent {
            val settings by settingsRepository.settings.collectAsStateWithLifecycle()
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (settings.themeMode) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemDark
            }

            WpsTheme(darkTheme = isDarkTheme) {
                MainAppContent(
                    deviceDiagnostics = deviceDiagnostics,
                    appLogger = appLogger,
                    settingsRepository = settingsRepository
                )
            }
        }
    }

    private fun requestRequiredPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            requestPermissionLauncher.launch(missing.toTypedArray())
        }
    }
}

@Composable
fun MainAppContent(
    deviceDiagnostics: DeviceDiagnostics,
    appLogger: AppLogger,
    settingsRepository: SettingsRepository
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Networks.route

    val screens = listOf(
        Screen.Networks,
        Screen.Diagnostics,
        Screen.Logs,
        Screen.Settings
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Two-Pane / NavigationRail layout for large screens/tablets
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.fillMaxHeight(),
                    header = {
                        Text(
                            text = "WPS/A",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                ) {
                    screens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) }
                        )
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = Screen.Networks.route,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    composable(Screen.Networks.route) {
                        HomeScreen()
                    }
                    composable(Screen.Diagnostics.route) {
                        DiagnosticsScreen(diagnostics = deviceDiagnostics)
                    }
                    composable(Screen.Logs.route) {
                        LogsScreen(appLogger = appLogger)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(settingsRepository = settingsRepository)
                    }
                }
            }
        } else {
            // Standard Phone NavigationBar layout
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar {
                        screens.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Networks.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Networks.route) {
                        HomeScreen()
                    }
                    composable(Screen.Diagnostics.route) {
                        DiagnosticsScreen(diagnostics = deviceDiagnostics)
                    }
                    composable(Screen.Logs.route) {
                        LogsScreen(appLogger = appLogger)
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(settingsRepository = settingsRepository)
                    }
                }
            }
        }
    }
}
