package com.nozero.shared.domain.model

/**
 * Aggregated consistency metrics for a habit.
 */
data class ConsistencyMetrics(
    val currentStreak: Int,
    val bestStreak: Int,
    val consistencyScore: Double,
    val totalCompleted: Int,
    val totalMissed: Int,
    val recoveryCount: Int,
    val isInRecovery: Boolean
)
