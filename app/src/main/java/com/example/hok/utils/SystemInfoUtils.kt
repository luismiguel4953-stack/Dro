package com.example.hok.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.view.WindowManager
import com.example.hok.data.SystemInfoModel

object SystemInfoUtils {

    fun getSystemInfo(context: Context): SystemInfoModel {
        // 1. RAM info
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        am?.getMemoryInfo(memoryInfo)

        val totalRam = memoryInfo.totalMem
        val availRam = memoryInfo.availMem
        val usedRam = (totalRam - availRam).coerceAtLeast(0L)
        val ramPercent = if (totalRam > 0) ((usedRam.toDouble() / totalRam.toDouble()) * 100).toInt() else 0

        // 2. Battery info
        val batteryStatusIntent: Intent? = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )

        var batteryPct = 100
        var isCharging = false
        var batteryStatusStr = "Normal"
        var batteryTempCelsius = 32.0f
        var batteryVolts = 3.8f

        batteryStatusIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level >= 0 && scale > 0) {
                batteryPct = ((level / scale.toFloat()) * 100).toInt()
            }

            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            batteryStatusStr = when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Cargando"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Descargando"
                BatteryManager.BATTERY_STATUS_FULL -> "Batería Llena"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Conectado (Sin cargar)"
                else -> "Operativo"
            }

            val tempTenths = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
            if (tempTenths > 0) {
                batteryTempCelsius = tempTenths / 10.0f
            }

            val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            if (voltage > 0) {
                batteryVolts = voltage / 1000.0f
            }
        }

        // 3. Thermal status via PowerManager
        val (thermalText, thermalCode) = getThermalStatus(context, batteryTempCelsius)

        // 4. Internal Storage info via StatFs
        val storageStats = getStorageInfo()

        // 5. Display Refresh Rate
        var refreshRate = 60.0f
        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            refreshRate = windowManager?.defaultDisplay?.refreshRate ?: 60.0f
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return SystemInfoModel(
            ramUsedBytes = usedRam,
            ramAvailableBytes = availRam,
            ramTotalBytes = totalRam,
            ramUsedPercent = ramPercent,
            thermalStatusText = thermalText,
            thermalStatusCode = thermalCode,
            batteryPercent = batteryPct,
            batteryStatusText = batteryStatusStr,
            isCharging = isCharging,
            batteryTemperatureCelsius = batteryTempCelsius,
            batteryVoltageVolts = batteryVolts,
            storageUsedBytes = storageStats.first,
            storageAvailableBytes = storageStats.second,
            storageTotalBytes = storageStats.third,
            storageUsedPercent = storageStats.fourth,
            deviceModel = Build.MODEL,
            deviceManufacturer = Build.MANUFACTURER.replaceFirstChar { it.uppercase() },
            androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
            sdkInt = Build.VERSION.SDK_INT,
            cpuAbi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a",
            cpuCores = Runtime.getRuntime().availableProcessors(),
            displayRefreshRate = refreshRate
        )
    }

    private fun getThermalStatus(context: Context, batteryTemp: Float): Pair<String, Int> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            powerManager?.let { pm ->
                val status = pm.currentThermalStatus
                return when (status) {
                    PowerManager.THERMAL_STATUS_NONE -> Pair("Óptima (Normal)", 0)
                    PowerManager.THERMAL_STATUS_LIGHT -> Pair("Ligera Elevación", 1)
                    PowerManager.THERMAL_STATUS_MODERATE -> Pair("Tibia (Moderada)", 2)
                    PowerManager.THERMAL_STATUS_SEVERE -> Pair("Caliente (Severa)", 3)
                    PowerManager.THERMAL_STATUS_CRITICAL -> Pair("Crítica (Peligro)", 4)
                    PowerManager.THERMAL_STATUS_EMERGENCY -> Pair("Emergencia Térmica", 5)
                    PowerManager.THERMAL_STATUS_SHUTDOWN -> Pair("Apagado por Calor", 6)
                    else -> Pair("Estable", 0)
                }
            }
        }

        // Fallback for API < 29 based on battery temperature
        return when {
            batteryTemp >= 45.0f -> Pair("Crítica (${batteryTemp}°C)", 4)
            batteryTemp >= 40.0f -> Pair("Caliente (${batteryTemp}°C)", 3)
            batteryTemp >= 36.0f -> Pair("Tibia (${batteryTemp}°C)", 2)
            else -> Pair("Óptima (${batteryTemp}°C)", 0)
        }
    }

    private fun getStorageInfo(): Quadruple<Long, Long, Long, Int> {
        return try {
            val path = Environment.getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            val availableBlocks = stat.availableBlocksLong

            val totalBytes = totalBlocks * blockSize
            val availBytes = availableBlocks * blockSize
            val usedBytes = (totalBytes - availBytes).coerceAtLeast(0L)

            val usedPercent = if (totalBytes > 0) {
                ((usedBytes.toDouble() / totalBytes.toDouble()) * 100).toInt()
            } else 0

            Quadruple(usedBytes, availBytes, totalBytes, usedPercent)
        } catch (e: Exception) {
            Quadruple(0L, 0L, 0L, 0)
        }
    }

    private data class Quadruple<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
