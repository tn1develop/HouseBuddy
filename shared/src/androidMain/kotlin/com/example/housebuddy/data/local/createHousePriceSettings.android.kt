package com.example.housebuddy.data.local

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

private const val PREFS_NAME = "house_price_prefs"

private lateinit var applicationContext: Context

fun initHousePriceSettings(context: Context) {
    applicationContext = context.applicationContext
}

internal actual fun createHousePriceSettings(): Settings {
    return SharedPreferencesSettings(
        applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    )
}
