package com.nozero.shared.data.local

import android.content.Context
import android.content.SharedPreferences

actual class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nozero_prefs", Context.MODE_PRIVATE)

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        prefs.getBoolean(key, default)

    actual fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }
}
