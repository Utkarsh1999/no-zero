package com.nozero.shared.data.local

import platform.Foundation.NSUserDefaults

actual class AppPreferences {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun getBoolean(key: String, default: Boolean): Boolean =
        if (defaults.objectForKey(key) != null) defaults.boolForKey(key) else default

    actual fun putBoolean(key: String, value: Boolean) {
        defaults.setBool(value, key)
    }
}
