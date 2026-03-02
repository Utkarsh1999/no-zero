package com.nozero.shared.motivation

import com.nozero.shared.domain.model.ConsistencyMetrics
import com.nozero.shared.domain.model.HabitType
import com.nozero.shared.domain.model.ReinforcementStyle

/**
 * Identity-based motivation engine.
 * Selects messaging based on habit type, current metrics, and user-chosen style.
 */
class MotivationEngine {

    fun getDailyMessage(
        habitTitle: String,
        habitType: HabitType,
        metrics: ConsistencyMetrics,
        style: ReinforcementStyle
    ): String {
        // 1. Recovery takes priority
        if (metrics.isInRecovery) {
            return getRecoveryMessage(habitType, style)
        }
        // 2. Milestone detection
        getMilestoneMessage(metrics.currentStreak)?.let { return it }
        // 3. Default identity-based message
        return getIdentityMessage(habitTitle, habitType, style)
    }

    private fun getRecoveryMessage(type: HabitType, style: ReinforcementStyle): String {
        return when (style) {
            ReinforcementStyle.SOFT -> when (type) {
                HabitType.GOOD -> "It's okay to miss a day. What matters is that you're here now."
                HabitType.BAD -> "A slip doesn't erase your progress. You're still on the path."
            }
            ReinforcementStyle.NEUTRAL -> when (type) {
                HabitType.GOOD -> "Recovery day. Completing today restores your momentum."
                HabitType.BAD -> "Relapse logged. Today is a fresh data point. Start again."
            }
            ReinforcementStyle.AGGRESSIVE -> when (type) {
                HabitType.GOOD -> "The comeback is always stronger. Don't let one miss become two."
                HabitType.BAD -> "You slipped. Now prove that was the last time. Show up today."
            }
        }
    }

    private fun getMilestoneMessage(streak: Int): String? {
        return when (streak) {
            3 -> "3 days in. The spark is lit. Keep feeding it."
            7 -> "One full week. You're building something real."
            14 -> "Two weeks of consistency. You're becoming reliable."
            30 -> "30 days. You've built a foundation. This is who you are now."
            60 -> "60 days. The habit is part of your identity."
            100 -> "100 days. Unstoppable. Pure discipline."
            else -> null
        }
    }

    private fun getIdentityMessage(
        title: String,
        type: HabitType,
        style: ReinforcementStyle
    ): String {
        return when (type) {
            HabitType.GOOD -> when (style) {
                ReinforcementStyle.SOFT -> "Every small step with '$title' is building a better you."
                ReinforcementStyle.NEUTRAL -> "Consistency with '$title' compounds over time."
                ReinforcementStyle.AGGRESSIVE -> "Show up for '$title'. No excuses. This is your identity."
            }
            HabitType.BAD -> when (style) {
                ReinforcementStyle.SOFT -> "You're becoming someone who doesn't need '$title'. Be proud."
                ReinforcementStyle.NEUTRAL -> "Another day without '$title'. The data speaks for itself."
                ReinforcementStyle.AGGRESSIVE -> "You're stronger than '$title'. Prove it again today."
            }
        }
    }
}
