package com.example.hok.data

import android.content.Context
import android.content.SharedPreferences

enum class GamingProfile {
    BALANCED,
    ULTRA_GAMING,
    POWER_SAVER
}

class GameModePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("hok_game_mode_prefs", Context.MODE_PRIVATE)

    var isGameModeActive: Boolean
        get() = prefs.getBoolean("is_game_mode_active", false)
        set(value) = prefs.edit().putBoolean("is_game_mode_active", value).apply()

    var autoCleanRamOnLaunch: Boolean
        get() = prefs.getBoolean("auto_clean_ram", true)
        set(value) = prefs.edit().putBoolean("auto_clean_ram", value).apply()

    var dndNotificationHintEnabled: Boolean
        get() = prefs.getBoolean("dnd_hint_enabled", true)
        set(value) = prefs.edit().putBoolean("dnd_hint_enabled", value).apply()

    var selectedProfile: GamingProfile
        get() {
            val name = prefs.getString("gaming_profile", GamingProfile.ULTRA_GAMING.name)
            return try {
                GamingProfile.valueOf(name ?: GamingProfile.ULTRA_GAMING.name)
            } catch (e: Exception) {
                GamingProfile.ULTRA_GAMING
            }
        }
        set(value) = prefs.edit().putString("gaming_profile", value.name).apply()

    var favoriteGamePackages: Set<String>
        get() = prefs.getStringSet("fav_game_pkgs", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("fav_game_pkgs", value).apply()

    var hasAcceptedFirstTimeNotice: Boolean
        get() = prefs.getBoolean("accepted_first_time_notice", false)
        set(value) = prefs.edit().putBoolean("accepted_first_time_notice", value).apply()
}
