package com.nozero.shared.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

/**
 * Core Habit entity.
 * Platform-agnostic. No Android/iOS dependencies.
 */
data class Habit(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: HabitType,
    val frequency: HabitFrequency,
    val trackingType: TrackingType,
    val reinforcementStyle: ReinforcementStyle = ReinforcementStyle.NEUTRAL,
    val reminderTime: LocalTime? = null,
    val isArchived: Boolean = false,
    val createdAt: Instant,
    val earnedGraceDays: Int = 0,
    val allowBackdateLogging: Boolean = true
)

/**
 * Whether the habit is something the user wants to DO or AVOID.
 */
enum class HabitType {
    GOOD, BAD
}

/**
 * How the habit is measured.
 */
sealed interface TrackingType {
    data object Binary : TrackingType
    data object Avoidance : TrackingType
    data class Count(val target: Int, val unit: String) : TrackingType
    data class Time(val targetMinutes: Int) : TrackingType
}

/**
 * When the habit should be performed.
 */
sealed interface HabitFrequency {
    data object Daily : HabitFrequency
    data class TimesPerWeek(val count: Int) : HabitFrequency
    data class Scheduled(val days: Set<DayOfWeek>) : HabitFrequency

    fun isScheduledFor(day: DayOfWeek): Boolean = when (this) {
        is Daily -> true
        is TimesPerWeek -> true
        is Scheduled -> days.contains(day)
    }
}

/**
 * The tone of motivational messaging.
 */
enum class ReinforcementStyle {
    SOFT, NEUTRAL, AGGRESSIVE
}
