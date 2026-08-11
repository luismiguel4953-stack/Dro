package com.example.hok.screens

import android.os.Build
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hok.utils.MemoryUtils
import com.example.hok.utils.SystemInfoUtils
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.NeonCyan

@Composable
fun DeviceInfoScreen() {
    val context = LocalContext.current
    val systemInfo = remember { SystemInfoUtils.getSystemInfo(context) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Section Title
        item {
            Column {
                Text(
                    text = "Información del Dispositivo",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = Color.White
                )
                Text(
                    text = "Especificaciones técnicas completas de hardware y sistema",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        // Hardware Summary Card
        item {
            InfoSectionCard(
                title = "DISPOSITIVO Y MARCA",
                icon = Icons.Default.PhoneAndroid,
                items = listOf(
                    "Modelo" to systemInfo.deviceModel,
                    "Fabricante" to systemInfo.deviceManufacturer,
                    "Dispositivo (Code)" to Build.DEVICE,
                    "Hardware" to Build.HARDWARE,
                    "Placa Base" to Build.BOARD
                )
            )
        }

        // Android System Spec Card
        item {
            InfoSectionCard(
                title = "SISTEMA OPERATIVO ANDROID",
                icon = Icons.Default.Android,
                items = listOf(
                    "Versión Android" to "Android ${Build.VERSION.RELEASE}",
                    "Nivel de API (SDK)" to "API ${systemInfo.sdkInt}",
                    "Versión del Parche" to (Build.VERSION.SECURITY_PATCH ?: "Actualizado"),
                    "ID de Compilación" to Build.DISPLAY,
                    "Bootloader" to Build.BOOTLOADER
                )
            )
        }

        // Processor & Architecture Card
        item {
            InfoSectionCard(
                title = "PROCESADOR Y GPU",
                icon = Icons.Default.Memory,
                items = listOf(
                    "Núcleos de CPU" to "${systemInfo.cpuCores} Núcleos Detectados",
                    "Arquitectura ABI" to systemInfo.cpuAbi,
                    "ABIs Soportadas" to Build.SUPPORTED_ABIS.joinToString(", "),
                    "Frecuencia Refresco" to "${systemInfo.displayRefreshRate.toInt()} Hz"
                )
            )
        }

        // RAM & Storage Spec Card
        item {
            InfoSectionCard(
                title = "MEMORIA Y BATERÍA",
                icon = Icons.Default.BatteryChargingFull,
                items = listOf(
                    "Total RAM" to MemoryUtils.formatBytes(systemInfo.ramTotalBytes),
                    "RAM Disponible" to MemoryUtils.formatBytes(systemInfo.ramAvailableBytes),
                    "Total Almacenamiento" to MemoryUtils.formatBytes(systemInfo.storageTotalBytes),
                    "Almacenamiento Libre" to MemoryUtils.formatBytes(systemInfo.storageAvailableBytes),
                    "Voltaje Batería" to "${systemInfo.batteryVoltageVolts} V"
                )
            )
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun InfoSectionCard(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, String>>
) {
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
                        imageVector = icon,
                        contentDescription = title,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            items.forEachIndexed { index, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pair.first,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFF94A3B8)
                    )
                    Text(
                        text = pair.second,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = Color.White
                    )
                }

                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(Color(0xFF1E293B))
                    )
                }
            }
        }
    }
}
