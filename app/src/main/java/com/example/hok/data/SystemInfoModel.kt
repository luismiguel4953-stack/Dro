package com.example.hok.data

data class SystemInfoModel(
    val ramUsedBytes: Long = 0L,
    val ramAvailableBytes: Long = 0L,
    val ramTotalBytes: Long = 0L,
    val ramUsedPercent: Int = 0,
    val thermalStatusText: String = "Normal",
    val thermalStatusCode: Int = 0, // 0 = Normal, 1 = Light, 2 = Moderate, 3 = Severe, 4 = Critical
    val batteryPercent: Int = 100,
    val batteryStatusText: String = "Descargando",
    val isCharging: Boolean = false,
    val batteryTemperatureCelsius: Float = 30.0f,
    val batteryVoltageVolts: Float = 3.8f,
    val storageUsedBytes: Long = 0L,
    val storageAvailableBytes: Long = 0L,
    val storageTotalBytes: Long = 0L,
    val storageUsedPercent: Int = 0,
    val deviceModel: String = "",
    val deviceManufacturer: String = "",
    val androidVersion: String = "",
    val sdkInt: Int = 0,
    val cpuAbi: String = "",
    val cpuCores: Int = 1,
    val displayRefreshRate: Float = 60.0f
)
