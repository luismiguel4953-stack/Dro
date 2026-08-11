package com.example.hok.data

import android.graphics.drawable.Drawable

data class GameAppModel(
    val packageName: String,
    val appName: String,
    val icon: Drawable? = null,
    val isFavorite: Boolean = false,
    val launchCount: Int = 0
)
