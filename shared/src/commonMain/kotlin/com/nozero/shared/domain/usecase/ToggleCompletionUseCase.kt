package com.nozero.shared.domain.usecase

import com.nozero.shared.domain.model.Completion
import com.nozero.shared.domain.model.CompletionStatus
import com.nozero.shared.domain.repository.CompletionRepository
import com.nozero.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlin.math.max
import kotlin.math.min

/**
 * Records or removes a habit's completion for a given date.
 * Supports binary toggle, value-based logging, and relapse recording.
 * Also evaluates and grants Grace Days based on streak consistency.
 */
class ToggleCompletionUseCase(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository,
    private val consistencyUseCase: CalculateConsistencyUseCase
) {
    /**
     * Binary toggle: if currently completed, delete; otherwise record as completed.
     */
    suspend operator fun invoke(
        habitId: String,
        date: LocalDate,
        currentlyCompleted: Boolean,
        value: Double = 1.0
    ) {
        withGraceDayEvaluation(habitId) {
            if (currentlyCompleted) {
                completionRepository.deleteCompletion(habitId, date)
            } else {
                completionRepository.recordCompletion(
                    Completion(
                        habitId = habitId,
                        date = date,
                        value = value,
                        status = CompletionStatus.COMPLETED
                    )
                )
            }
        }
    }

    /**
     * Value-based log: always writes (overwrites) a completion with the given value and status.
     * Used for Time, Count, and Avoidance tracking types.
     */
    suspend fun logValue(
        habitId: String,
        date: LocalDate,
        value: Double,
        status: CompletionStatus
    ) {
        withGraceDayEvaluation(habitId) {
            // Delete existing first to ensure clean overwrite
            completionRepository.deleteCompletion(habitId, date)
            completionRepository.recordCompletion(
                Completion(
                    habitId = habitId,
                    date = date,
                    value = value,
                    status = status
                )
            )
        }
    }

    private suspend fun withGraceDayEvaluation(habitId: String, action: suspend () -> Unit) {
        val habit = habitRepository.getHabitById(habitId).first() ?: return
        val historyBefore = completionRepository.getCompletionsForHabit(habitId).first()
        val metricsBefore = consistencyUseCase.execute(historyBefore)

        // Perform the actual logging/deleting action
        action()

        val historyAfter = completionRepository.getCompletionsForHabit(habitId).first()
        val metricsAfter = consistencyUseCase.execute(historyAfter)

        // Evaluate earned grace days based on intervals of 14 days
        val interval = 14
        val earnedBefore = metricsBefore.currentStreak / interval
        val earnedAfter = metricsAfter.currentStreak / interval

        var currentGraceDays = habit.earnedGraceDays

        if (earnedAfter > earnedBefore) {
            val gained = earnedAfter - earnedBefore
            currentGraceDays = min(3, currentGraceDays + gained)
        } else if (earnedAfter < earnedBefore) {
            val lost = earnedBefore - earnedAfter
            currentGraceDays = max(0, currentGraceDays - lost)
        }

        if (currentGraceDays != habit.earnedGraceDays) {
            habitRepository.updateHabit(habit.copy(earnedGraceDays = currentGraceDays))
        }
    }
}
