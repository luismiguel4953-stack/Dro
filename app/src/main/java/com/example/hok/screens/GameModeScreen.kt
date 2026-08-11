package com.example.hok.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hok.components.GameLauncherItemCard
import com.example.hok.components.PerformanceTipCard
import com.example.hok.components.SafetyWarningDialog
import com.example.hok.data.GameAppModel
import com.example.hok.data.GameModePreferences
import com.example.hok.data.GamingProfile
import com.example.hok.utils.MemoryUtils
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.ElectricRed
import com.example.ui.theme.NeonCyan

@Composable
fun GameModeScreen() {
    val context = LocalContext.current
    val prefs = remember { GameModePreferences(context) }

    var isGameModeActive by remember { mutableStateOf(prefs.isGameModeActive) }
    var selectedProfile by remember { mutableStateOf(prefs.selectedProfile) }
    var autoCleanRam by remember { mutableStateOf(prefs.autoCleanRamOnLaunch) }
    var showFirstTimeDialog by remember { mutableStateOf(false) }

    val installedGames = remember { mutableStateListOf<GameAppModel>() }

    // Load installed game applications safely
    LaunchedEffect(Unit) {
        try {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfos = pm.queryIntentActivities(intent, 0)
            val favs = prefs.favoriteGamePackages

            val apps = resolveInfos.mapNotNull { info ->
                val pkgName = info.activityInfo.packageName
                if (pkgName == context.packageName) return@mapNotNull null
                val label = info.loadLabel(pm).toString()
                GameAppModel(
                    packageName = pkgName,
                    appName = label,
                    isFavorite = favs.contains(pkgName)
                )
            }.take(15)

            installedGames.clear()
            installedGames.addAll(apps)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    if (showFirstTimeDialog) {
        SafetyWarningDialog(
            title = "Aviso del Modo Juego HOK",
            message = "El Modo Juego optimiza los recursos de tu dispositivo para brindarte la mejor estabilidad durante las partidas.",
            infoPoints = listOf(
                "HOK libera la memoria interna asignada y prepara el entorno antes de lanzar el juego.",
                "No requiere permisos de Root ni altera archivos del sistema.",
                "No promete aumentos ficticios de FPS: optimiza los procesos reales permitidos por Android.",
                "Puedes activar o desactivar este modo en cualquier momento."
            ),
            confirmButtonText = "Entendido y Activar",
            cancelButtonText = "Cancelar",
            onConfirm = {
                prefs.hasAcceptedFirstTimeNotice = true
                prefs.isGameModeActive = true
                isGameModeActive = true
                showFirstTimeDialog = false
                Toast.makeText(context, "Modo Juego HOK Activado", Toast.LENGTH_SHORT).show()
            },
            onDismiss = {
                showFirstTimeDialog = false
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Section Title
        item {
            Column {
                Text(
                    text = "Sección Modo Juego",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "Aceleración y entorno optimizado sin Root",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Toggle Switch Main Banner
        item {
            val bannerBg by animateColorAsState(
                targetValue = if (isGameModeActive) Color(0xFF002B36) else Color(0xFF131A2A),
                label = "banner_bg"
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = bannerBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.5.dp,
                        color = if (isGameModeActive) NeonCyan else Color(0xFF1E293B),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isGameModeActive) NeonCyan else Color(0xFF1E293B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = "Modo Juego",
                                tint = if (isGameModeActive) Color.Black else Color(0xFF94A3B8),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "ESTADO MODO JUEGO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = if (isGameModeActive) "MODO JUEGO ACTIVADO" else "MODO DESACTIVADO",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isGameModeActive) Color.White else Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Switch(
                        checked = isGameModeActive,
                        onCheckedChange = { active ->
                            if (active && !prefs.hasAcceptedFirstTimeNotice) {
                                showFirstTimeDialog = true
                            } else {
                                prefs.isGameModeActive = active
                                isGameModeActive = active
                                val msg = if (active) "Modo Juego Activado" else "Modo Juego Desactivado"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Black,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = Color(0xFF64748B),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }
        }

        // Profile Selector Cards
        item {
            Column {
                Text(
                    text = "Perfil de Rendimiento",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GamingProfile.values().forEach { profile ->
                        val isSelected = selectedProfile == profile
                        val label = when (profile) {
                            GamingProfile.ULTRA_GAMING -> "Ultra Gaming"
                            GamingProfile.BALANCED -> "Equilibrado"
                            GamingProfile.POWER_SAVER -> "Ahorro Batería"
                        }
                        val profileColor = when (profile) {
                            GamingProfile.ULTRA_GAMING -> NeonCyan
                            GamingProfile.BALANCED -> AccentGreen
                            GamingProfile.POWER_SAVER -> ElectricRed
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) profileColor.copy(alpha = 0.2f) else Color(0xFF131A2A))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) profileColor else Color(0xFF1E293B),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedProfile = profile
                                    prefs.selectedProfile = profile
                                    Toast.makeText(context, "Perfil cambiado a $label", Toast.LENGTH_SHORT).show()
                                }
                                .padding(vertical = 12.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }
        }

        // Safe Optimization Actions
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Funciones Seguras HOK",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Auto-Limpieza al Lanzar Juego",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            Text(
                                text = "Ejecuta recolección de basura interna antes de iniciar la partida",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = Color(0xFF64748B)
                            )
                        }

                        Switch(
                            checked = autoCleanRam,
                            onCheckedChange = { checked ->
                                autoCleanRam = checked
                                prefs.autoCleanRamOnLaunch = checked
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = NeonCyan
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val freed = MemoryUtils.performSafeHokOptimization(context)
                            val freedStr = MemoryUtils.formatBytes(freed)
                            Toast.makeText(context, "Entorno de juego preparado. Memoria liberada: $freedStr", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = NeonCyan
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Liberar Recursos Internos Ahora",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // Game Launcher Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lanzador de Juegos Con Boost",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Selecciona tus juegos instalados para ejecutarlos con optimización",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Installed Game List
        if (installedGames.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando o no se encontraron juegos en el dispositivo",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        } else {
            items(installedGames) { game ->
                GameLauncherItemCard(
                    game = game,
                    onLaunchClick = { pkg ->
                        if (autoCleanRam) {
                            MemoryUtils.performSafeHokOptimization(context)
                        }
                        try {
                            val launchIntent = context.packageManager.getLaunchIntentForPackage(pkg)
                            if (launchIntent != null) {
                                Toast.makeText(context, "Lanzando ${game.appName} con Boost HOK...", Toast.LENGTH_SHORT).show()
                                context.startActivity(launchIntent)
                            } else {
                                Toast.makeText(context, "No se puede abrir este paquete", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al lanzar el juego", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onToggleFavorite = { pkg ->
                        val currentFavs = prefs.favoriteGamePackages.toMutableSet()
                        if (currentFavs.contains(pkg)) {
                            currentFavs.remove(pkg)
                        } else {
                            currentFavs.add(pkg)
                        }
                        prefs.favoriteGamePackages = currentFavs

                        val index = installedGames.indexOfFirst { it.packageName == pkg }
                        if (index >= 0) {
                            val item = installedGames[index]
                            installedGames[index] = item.copy(isFavorite = !item.isFavorite)
                        }
                    }
                )
            }
        }

        // Performance Recommendations Section
        item {
            Text(
                text = "Recomendaciones de Rendimiento Real",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        item {
            PerformanceTipCard(
                title = "Ajusta la Tasa de Refresco a la máxima permitida",
                description = "En dispositivos con pantallas de 90Hz o 120Hz, abre la configuración de Pantalla para fijar la tasa alta y obtener fluidez táctil instantánea."
            )
        }

        item {
            PerformanceTipCard(
                title = "Cierra apps no esenciales antes de jugar",
                description = "Android administra la RAM de forma nativa. Cerrar manualmente pestañas pesadas del navegador ayuda a mantener la RAM libre para el juego."
            )
        }

        item {
            PerformanceTipCard(
                title = "Mantén la temperatura bajo 40°C",
                description = "El sobrecalentamiento causa Thermal Throttling (bajada de Hz del procesador). Juega sin funda en ambientes frescos para evitar caídas de FPS."
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
