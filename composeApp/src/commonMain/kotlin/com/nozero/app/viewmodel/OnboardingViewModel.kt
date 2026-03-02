package com.nozero.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nozero.shared.data.local.AppPreferences
import com.nozero.shared.data.local.PreferenceKeys
import com.nozero.shared.domain.model.*
import com.nozero.shared.domain.usecase.CreateHabitUseCase
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime

class OnboardingViewModel(
    private val preferences: AppPreferences,
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {

    fun isOnboardingCompleted(): Boolean {
        return preferences.getBoolean(PreferenceKeys.ONBOARDING_COMPLETED, false)
    }

    fun completeOnboarding() {
        preferences.putBoolean(PreferenceKeys.ONBOARDING_COMPLETED, true)
    }

    fun createHabitsFromTemplates(templatesWithTimes: List<Pair<HabitTemplate, LocalTime?>>) {
        viewModelScope.launch {
            templatesWithTimes.forEach { (template, customTime) ->
                val habit = Habit(
                    id = generateId(),
                    title = template.title,
                    description = template.description,
                    type = template.type,
                    frequency = template.frequency,
                    trackingType = template.trackingType,
                    reinforcementStyle = template.reinforcementStyle,
                    reminderTime = customTime,
                    createdAt = Clock.System.now(),
                    allowBackdateLogging = true
                )
                createHabitUseCase(habit)
            }
        }
    }

    fun createCustomHabit(
        title: String,
        description: String?,
        type: HabitType,
        frequency: HabitFrequency,
        trackingType: TrackingType,
        reinforcementStyle: ReinforcementStyle,
        reminderTime: LocalTime? = null,
        allowBackdateLogging: Boolean = true
    ) {
        viewModelScope.launch {
            val habit = Habit(
                id = generateId(),
                title = title,
                description = description,
                type = type,
                frequency = frequency,
                trackingType = trackingType,
                reinforcementStyle = reinforcementStyle,
                reminderTime = reminderTime,
                createdAt = Clock.System.now(),
                allowBackdateLogging = allowBackdateLogging
            )
            createHabitUseCase(habit)
        }
    }

    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString() + "-" +
                (0..9999).random().toString().padStart(4, '0')
    }
}
