package com.example.hok.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.hok.components.MetricGaugeCard
import com.example.hok.components.RamStatCard
import com.example.hok.data.SystemInfoModel
import com.example.hok.utils.MemoryUtils
import com.example.hok.utils.SystemInfoUtils
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentYellow
import com.example.ui.theme.ElectricRed
import com.example.ui.theme.NeonCyan
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    onNavigateToGameMode: () -> Unit = {}
) {
    val context = LocalContext.current
    var systemInfo by remember { mutableStateOf(SystemInfoUtils.getSystemInfo(context)) }
    var isOptimizing by remember { mutableStateOf(false) }

    // Auto-refresh metrics every 3 seconds
    LaunchedEffect(Unit) {
        while (true) {
            systemInfo = SystemInfoUtils.getSystemInfo(context)
            delay(3000)
        }
    }

    val ramUsedStr = remember(systemInfo.ramUsedBytes) {
        MemoryUtils.formatBytes(systemInfo.ramUsedBytes)
    }
    val ramAvailStr = remember(systemInfo.ramAvailableBytes) {
        MemoryUtils.formatBytes(systemInfo.ramAvailableBytes)
    }
    val storageUsedStr = remember(systemInfo.storageUsedBytes) {
        MemoryUtils.formatBytes(systemInfo.storageUsedBytes)
    }
    val storageTotalStr = remember(systemInfo.storageTotalBytes) {
        MemoryUtils.formatBytes(systemInfo.storageTotalBytes)
    }

    val thermalColor = when (systemInfo.thermalStatusCode) {
        0 -> AccentGreen
        1, 2 -> AccentYellow
        else -> ElectricRed
    }

    val batteryColor = when {
        systemInfo.batteryPercent < 20 -> ElectricRed
        systemInfo.batteryPercent < 50 -> AccentYellow
        else -> AccentGreen
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Panel de Rendimiento",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                        color = Color.White
                    )
                    Text(
                        text = "Estado del dispositivo en tiempo real",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }

                IconButton(
                    onClick = {
                        systemInfo = SystemInfoUtils.getSystemInfo(context)
                        Toast.makeText(context, "Métricas actualizadas", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF131A2A))
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Actualizar",
                        tint = NeonCyan
                    )
                }
            }
        }

        // RAM Main Card
        item {
            RamStatCard(
                usedBytesFormatted = ramUsedStr,
                availableBytesFormatted = ramAvailStr,
                usedPercent = systemInfo.ramUsedPercent
            )
        }

        // Quick Optimization Button
        item {
            Button(
                onClick = {
                    isOptimizing = true
                    val freed = MemoryUtils.performSafeHokOptimization(context)
                    val freedStr = MemoryUtils.formatBytes(freed)
                    systemInfo = SystemInfoUtils.getSystemInfo(context)
                    isOptimizing = false
                    Toast.makeText(context, "¡Optimizatorio! Memoria liberada: $freedStr", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonCyan,
                    contentColor = Color.Black
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOptimizing) "OPTIMIZANDO..." else "OPTIMIZAR MEMORIA HOK",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold)
                    )
                }
            }
        }

        // Grid Metrics: Battery & Thermal
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGaugeCard(
                    title = "BATERÍA",
                    value = "${systemInfo.batteryPercent}%",
                    subValue = "${systemInfo.batteryStatusText} (${systemInfo.batteryTemperatureCelsius}°C)",
                    icon = Icons.Default.BatteryChargingFull,
                    statusColor = batteryColor,
                    percent = systemInfo.batteryPercent,
                    modifier = Modifier.weight(1f)
                )

                MetricGaugeCard(
                    title = "TÉRMICO",
                    value = systemInfo.thermalStatusText,
                    subValue = "Temp Batería: ${systemInfo.batteryTemperatureCelsius}°C",
                    icon = Icons.Default.Thermostat,
                    statusColor = thermalColor,
                    percent = (systemInfo.batteryTemperatureCelsius * 2).toInt().coerceIn(0, 100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Storage & Display Refresh Rate
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricGaugeCard(
                    title = "ALMACENAMIENTO",
                    value = "$storageUsedStr / $storageTotalStr",
                    subValue = "${systemInfo.storageUsedPercent}% Ocupado",
                    icon = Icons.Default.SdCard,
                    statusColor = NeonCyan,
                    percent = systemInfo.storageUsedPercent,
                    modifier = Modifier.weight(1f)
                )

                MetricGaugeCard(
                    title = "PANTALLA",
                    value = "${systemInfo.displayRefreshRate.toInt()} Hz",
                    subValue = "Tasa de refresco activa",
                    icon = Icons.Default.DeveloperBoard,
                    statusColor = AccentGreen,
                    percent = ((systemInfo.displayRefreshRate / 120f) * 100).toInt().coerceIn(10, 100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Processor & Device Specs Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2A)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NeonCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "CPU",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "PROCESADOR Y HARDWARE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                ),
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "${systemInfo.deviceManufacturer} ${systemInfo.deviceModel}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Sistema:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                            Text(text = systemInfo.androidVersion, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = Color.White)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Núcleos CPU:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                            Text(text = "${systemInfo.cpuCores} Cores (${systemInfo.cpuAbi})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = AccentGreen)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
