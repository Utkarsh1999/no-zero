package com.nozero.app.viewmodel

import com.nozero.shared.domain.model.ConsistencyMetrics
import com.nozero.shared.domain.model.Habit
import com.nozero.shared.domain.model.CompletionStatus
import kotlinx.datetime.LocalDate

/**
 * UI model for a single day cell in the calendar grid.
 */
data class DayCell(
    val date: LocalDate,
    val status: CompletionStatus?,
    val isToday: Boolean,
    val isFuture: Boolean
)

/**
 * Milestone along the Identity Path.
 */
data class MilestoneUiModel(
    val title: String,
    val description: String,
    val isAchieved: Boolean
)

/**
 * State model for the Habit Detail screen.
 */
sealed interface HabitDetailUiState {
    data object Loading : HabitDetailUiState
    data class Success(
        val habit: Habit,
        val metrics: ConsistencyMetrics,
        val calendarDays: List<DayCell>,
        val milestones: List<MilestoneUiModel>,
        val motivationMessage: String
    ) : HabitDetailUiState
    data class Error(val message: String) : HabitDetailUiState
}
