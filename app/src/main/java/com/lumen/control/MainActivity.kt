package com.lumen.control

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lumen.control.ir.IrTransmitter
import com.lumen.control.ui.screens.DevicesScreen
import com.lumen.control.ui.screens.LightControlScreen
import com.lumen.control.ui.screens.SettingsScreen
import com.lumen.control.ui.theme.LumenTheme
import com.lumen.control.ui.theme.Surface as SurfaceColor

private sealed class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    data object Lights : Tab("lights", "Lights", Icons.Filled.Lightbulb)
    data object Devices : Tab("devices", "Devices", Icons.Filled.Widgets)
    data object Settings : Tab("settings", "Settings", Icons.Filled.Settings)
}

private val tabs = listOf(Tab.Lights, Tab.Devices, Tab.Settings)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val irTransmitter = IrTransmitter(applicationContext)

        setContent {
            LumenTheme {
                LumenApp(irTransmitter)
            }
        }
    }
}

@Composable
private fun LumenApp(irTransmitter: IrTransmitter) {
    val navController = rememberNavController()

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFF0B0B12),
        bottomBar = {
            NavigationBar(containerColor = SurfaceColor) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Tab.Lights.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Tab.Lights.route) { LightControlScreen(irTransmitter) }
            composable(Tab.Devices.route) { DevicesScreen() }
            composable(Tab.Settings.route) { SettingsScreen(irTransmitter) }
        }
    }
}
