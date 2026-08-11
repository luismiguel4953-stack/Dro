package com.example.hok.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

data class SettingShortcutItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val explanationText: String,
    val intentAction: String
)

object IntentUtils {

    val SETTINGS_SHORTCUTS = listOf(
        SettingShortcutItem(
            id = "app_info",
            title = "Información de Aplicaciones",
            subtitle = "Gestiona permisos, almacenamiento y datos de juegos",
            iconName = "apps",
            explanationText = "Se abrirá la pantalla de Ajustes de Aplicaciones de Android. Desde allí puedes gestionar los permisos en segundo plano y limpiar el almacenamiento caché de tus juegos.",
            intentAction = Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
        ),
        SettingShortcutItem(
            id = "battery",
            title = "Ajustes de Batería",
            subtitle = "Optimización de energía y consumo térmico",
            iconName = "battery",
            explanationText = "Se abrirá la sección de Batería del sistema. Te recomendamos desactivar la restricción de batería para tus juegos favoritos y evitar cierres inesperados en medio de la partida.",
            intentAction = Settings.ACTION_BATTERY_SAVER_SETTINGS
        ),
        SettingShortcutItem(
            id = "display",
            title = "Pantalla y Tasa de Refresco",
            subtitle = "Ajusta brillo, Hz de pantalla y tiempo de espera",
            iconName = "display",
            explanationText = "Se abrirá la configuración de Pantalla. Para conseguir la mayor fluidez en juegos, asegúrate de activar la tasa de refresco más alta disponible (90Hz, 120Hz o 144Hz).",
            intentAction = Settings.ACTION_DISPLAY_SETTINGS
        ),
        SettingShortcutItem(
            id = "wifi",
            title = "Ajustes de Wi-Fi y Red",
            subtitle = "Optimiza la latencia (PING) y la conexión de juego",
            iconName = "wifi",
            explanationText = "Se abrirán las preferencias de Wi-Fi de Android para verificar si estás conectado a una banda de 5GHz para menor PING y latencia reducida.",
            intentAction = Settings.ACTION_WIFI_SETTINGS
        ),
        SettingShortcutItem(
            id = "bluetooth",
            title = "Ajustes de Bluetooth",
            subtitle = "Conecta mandos, gampads y auriculares gamer",
            iconName = "bluetooth",
            explanationText = "Se abrirá la configuración de Bluetooth para emparejar y verificar tus mandos inalámbricos o auriculares con baja latencia de audio.",
            intentAction = Settings.ACTION_BLUETOOTH_SETTINGS
        ),
        SettingShortcutItem(
            id = "storage",
            title = "Almacenamiento del Dispositivo",
            subtitle = "Liberación de espacio interno para instalar juegos",
            iconName = "storage",
            explanationText = "Se abrirá el administrador de almacenamiento oficial de Android para revisar qué juegos u archivos están ocupando espacio interno.",
            intentAction = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
        ),
        SettingShortcutItem(
            id = "developer",
            title = "Opciones de Desarrollador",
            subtitle = "Ajustes avanzados de renderizado y aceleración GPU",
            iconName = "developer",
            explanationText = "Se abrirá la sección Opciones de Desarrollador del sistema (si está habilitada en tu dispositivo). Útil para ajustar escalas de animación a 0.5x o verificar controladores gráficos.",
            intentAction = Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
        )
    )

    fun openSettingIntent(context: Context, shortcut: SettingShortcutItem) {
        try {
            val intent = Intent(shortcut.intentAction).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Fallback to general settings if specific action not supported on device
            try {
                val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "No se pudo abrir este ajuste en tu dispositivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Ajuste no disponible en este sistema", Toast.LENGTH_SHORT).show()
        }
    }

    fun openAppDetails(context: Context, packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No se pudo abrir el detalle de la app", Toast.LENGTH_SHORT).show()
        }
    }
}
