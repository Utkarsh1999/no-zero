package com.nozero.shared.data.mapper

import com.nozero.shared.data.local.HabitEntity
import com.nozero.shared.domain.model.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = id,
        title = title,
        description = description,
        type = HabitType.valueOf(type),
        frequency = mapFrequency(frequencyType, frequencyValue),
        trackingType = mapTrackingType(trackingType, trackingTarget),
        reinforcementStyle = ReinforcementStyle.valueOf(reinforcementStyle),
        reminderTime = reminderTime?.let { LocalTime.parse(it) },
        isArchived = isArchived != 0L,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        earnedGraceDays = earnedGraceDays.toInt(),
        allowBackdateLogging = allowBackdateLogging != 0L
    )
}

fun Habit.toEntity(): HabitEntityData {
    return HabitEntityData(
        id = id,
        title = title,
        description = description,
        type = type.name,
        frequencyType = frequency.toTypeString(),
        frequencyValue = frequency.toValueString(),
        trackingType = trackingType.toTypeString(),
        trackingTarget = trackingType.toTargetString(),
        reinforcementStyle = reinforcementStyle.name,
        reminderTime = reminderTime?.toString(),
        isArchived = if (isArchived) 1L else 0L,
        createdAt = createdAt.toEpochMilliseconds(),
        earnedGraceDays = earnedGraceDays.toLong(),
        allowBackdateLogging = if (allowBackdateLogging) 1L else 0L
    )
}

/**
 * Intermediate data class to avoid coupling to SQLDelight generated types in the mapper signature.
 */
data class HabitEntityData(
    val id: String,
    val title: String,
    val description: String?,
    val type: String,
    val frequencyType: String,
    val frequencyValue: String?,
    val trackingType: String,
    val trackingTarget: String?,
    val reinforcementStyle: String,
    val reminderTime: String?,
    val isArchived: Long,
    val createdAt: Long,
    val earnedGraceDays: Long,
    val allowBackdateLogging: Long
)

private fun mapFrequency(type: String, value: String?): HabitFrequency = when (type) {
    "DAILY" -> HabitFrequency.Daily
    "TIMES_PER_WEEK" -> HabitFrequency.TimesPerWeek(value?.toIntOrNull() ?: 1)
    "SCHEDULED" -> HabitFrequency.Scheduled(
        value?.split(",")?.mapNotNull { runCatching { DayOfWeek.valueOf(it.trim()) }.getOrNull() }?.toSet() ?: emptySet()
    )
    else -> HabitFrequency.Daily
}

private fun mapTrackingType(type: String, target: String?): TrackingType = when (type) {
    "BINARY" -> TrackingType.Binary
    "AVOIDANCE" -> TrackingType.Avoidance
    "COUNT" -> {
        val parts = target?.split(":") ?: listOf("1", "times")
        TrackingType.Count(parts.getOrNull(0)?.toIntOrNull() ?: 1, parts.getOrNull(1) ?: "times")
    }
    "TIME" -> TrackingType.Time(target?.toIntOrNull() ?: 30)
    else -> TrackingType.Binary
}

private fun HabitFrequency.toTypeString(): String = when (this) {
    is HabitFrequency.Daily -> "DAILY"
    is HabitFrequency.TimesPerWeek -> "TIMES_PER_WEEK"
    is HabitFrequency.Scheduled -> "SCHEDULED"
}

private fun HabitFrequency.toValueString(): String? = when (this) {
    is HabitFrequency.Daily -> null
    is HabitFrequency.TimesPerWeek -> count.toString()
    is HabitFrequency.Scheduled -> days.joinToString(",") { it.name }
}

private fun TrackingType.toTypeString(): String = when (this) {
    is TrackingType.Binary -> "BINARY"
    is TrackingType.Avoidance -> "AVOIDANCE"
    is TrackingType.Count -> "COUNT"
    is TrackingType.Time -> "TIME"
}

private fun TrackingType.toTargetString(): String? = when (this) {
    is TrackingType.Binary, is TrackingType.Avoidance -> null
    is TrackingType.Count -> "$target:$unit"
    is TrackingType.Time -> targetMinutes.toString()
}
