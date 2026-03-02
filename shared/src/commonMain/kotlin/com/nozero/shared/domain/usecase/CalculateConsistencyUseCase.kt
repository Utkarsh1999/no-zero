package com.nozero.shared.domain.usecase

import com.nozero.shared.domain.model.Completion
import com.nozero.shared.domain.model.CompletionStatus
import com.nozero.shared.domain.model.ConsistencyMetrics

/**
 * Pure domain logic: calculates streaks, consistency scores, and recovery state.
 * No platform dependencies. Fully testable.
 */
class CalculateConsistencyUseCase {

    companion object {
        private const val BASE_INCREMENT = 0.03
        private const val MISS_PENALTY = 0.05
        private const val RECOVERY_BONUS = 0.04
    }

    fun execute(history: List<Completion>): ConsistencyMetrics {
        val sorted = history.sortedBy { it.date }
        if (sorted.isEmpty()) {
            return ConsistencyMetrics(
                currentStreak = 0,
                bestStreak = 0,
                consistencyScore = 0.0,
                totalCompleted = 0,
                totalMissed = 0,
                recoveryCount = 0,
                isInRecovery = false
            )
        }

        var score = 0.0
        var currentStreak = 0
        var bestStreak = 0
        var totalCompleted = 0
        var totalMissed = 0
        var recoveryCount = 0
        var previouslyMissed = false

        for (entry in sorted) {
            when (entry.status) {
                CompletionStatus.COMPLETED -> {
                    totalCompleted++
                    currentStreak++
                    score += BASE_INCREMENT
                    if (previouslyMissed) {
                        recoveryCount++
                        score += RECOVERY_BONUS
                        previouslyMissed = false
                    }
                }
                CompletionStatus.MISSED, CompletionStatus.RELAPSED -> {
                    totalMissed++
                    if (currentStreak > bestStreak) bestStreak = currentStreak
                    currentStreak = 0
                    score -= MISS_PENALTY
                    previouslyMissed = true
                }
                CompletionStatus.EXEMPTED -> {
                    currentStreak++
                    // Neutral: does not break streak, does not affect score
                }
            }
            score = score.coerceIn(0.0, 1.0)
        }
        if (currentStreak > bestStreak) bestStreak = currentStreak

        val lastStatus = sorted.lastOrNull()?.status
        val isInRecovery = lastStatus == CompletionStatus.MISSED || lastStatus == CompletionStatus.RELAPSED

        return ConsistencyMetrics(
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            consistencyScore = score,
            totalCompleted = totalCompleted,
            totalMissed = totalMissed,
            recoveryCount = recoveryCount,
            isInRecovery = isInRecovery
        )
    }
}
