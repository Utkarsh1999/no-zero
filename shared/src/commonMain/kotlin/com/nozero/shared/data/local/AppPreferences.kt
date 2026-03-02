package com.nozero.shared.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Platform-agnostic key–value preference store.
 * Used to persist flags like onboarding completion.
 */
expect class AppPreferences {
    fun getBoolean(key: String, default: Boolean): Boolean
    fun putBoolean(key: String, value: Boolean)
}

object PreferenceKeys {
    const val ONBOARDING_COMPLETED = "onboarding_completed"
    const val TUTORIAL_COMPLETED = "tutorial_completed"
}
