package com.nozero.app.viewmodel

import com.nozero.shared.domain.model.HabitType
import com.nozero.shared.domain.model.TrackingType

/**
 * UI model for a single habit card on the Today screen.
 */
data class HabitItemUiModel(
    val id: String,
    val title: String,
    val type: HabitType,
    val trackingType: TrackingType,
    val currentStreak: Int,
    val consistencyScore: Double,
    val isCompletedToday: Boolean,
    val loggedValue: Double,
    val motivationMessage: String
)

/**
 * State model for the Today screen (Unidirectional Data Flow).
 */
sealed interface TodayUiState {
    data object Loading : TodayUiState
    data class Success(
        val dateLabel: String,
        val habits: List<HabitItemUiModel>,
        val dailyMotivation: String
    ) : TodayUiState
    data class Error(val message: String) : TodayUiState
}
