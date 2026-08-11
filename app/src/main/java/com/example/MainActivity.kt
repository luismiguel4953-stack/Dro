package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hok.components.HokHeaderBar
import com.example.hok.data.GameModePreferences
import com.example.hok.screens.DashboardScreen
import com.example.hok.screens.DeviceInfoScreen
import com.example.hok.screens.GameModeScreen
import com.example.hok.screens.PhoneSettingsScreen
import com.example.hok.screens.SplashScreen
import com.example.ui.theme.HokTheme
import com.example.ui.theme.NeonCyan

enum class HokTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    DASHBOARD("Panel", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
    GAME_MODE("Modo Juego", Icons.Default.FlashOn, Icons.Outlined.FlashOn),
    SETTINGS("Ajustes", Icons.Default.Settings, Icons.Outlined.Settings),
    DEVICE_INFO("Hardware", Icons.Default.Info, Icons.Outlined.Info)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HokTheme {
                HokMainAppHost()
            }
        }
    }
}

@Composable
fun HokMainAppHost() {
    val context = LocalContext.current
    val prefs = remember { GameModePreferences(context) }

    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(HokTab.DASHBOARD) }
    var isGameModeActive by remember { mutableStateOf(prefs.isGameModeActive) }

    if (showSplash) {
        SplashScreen(
            onStartClick = {
                showSplash = false
            }
        )
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            containerColor = Color(0xFF090D16),
            topBar = {
                HokHeaderBar(
                    isGameModeActive = prefs.isGameModeActive,
                    onToggleGameMode = {
                        val newStatus = !prefs.isGameModeActive
                        prefs.isGameModeActive = newStatus
                        isGameModeActive = newStatus
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    HokTab.values().forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                isGameModeActive = prefs.isGameModeActive
                                currentTab = tab
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) NeonCyan else Color(0xFF64748B)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) NeonCyan else Color(0xFF64748B)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF003847)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFF090D16))
            ) {
                Crossfade(
                    targetState = currentTab,
                    animationSpec = tween(durationMillis = 250),
                    label = "tab_crossfade"
                ) { tab ->
                    when (tab) {
                        HokTab.DASHBOARD -> DashboardScreen(
                            onNavigateToGameMode = { currentTab = HokTab.GAME_MODE }
                        )
                        HokTab.GAME_MODE -> GameModeScreen()
                        HokTab.SETTINGS -> PhoneSettingsScreen()
                        HokTab.DEVICE_INFO -> DeviceInfoScreen()
                    }
                }
            }
        }
    }
}
