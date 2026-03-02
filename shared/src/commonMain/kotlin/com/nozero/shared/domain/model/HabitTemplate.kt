package com.nozero.shared.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

/**
 * A pre-defined habit template for quick onboarding.
 */
data class HabitTemplate(
    val title: String,
    val description: String,
    val type: HabitType,
    val frequency: HabitFrequency,
    val trackingType: TrackingType,
    val reinforcementStyle: ReinforcementStyle,
    val motivationCopy: String,
    val recoveryCopy: String,
    val suggestedReminderTime: LocalTime?
)

/**
 * Pre-defined habit templates categorized by type.
 */
object HabitTemplates {

    val goodHabits: List<HabitTemplate> = listOf(
        HabitTemplate(
            title = "Gym",
            description = "Hit the gym and move your body",
            type = HabitType.GOOD,
            frequency = HabitFrequency.Scheduled(
                setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
            ),
            trackingType = TrackingType.Binary,
            reinforcementStyle = ReinforcementStyle.NEUTRAL,
            motivationCopy = "You are becoming an athlete.",
            recoveryCopy = "Rest days are part of the game. Get back at it.",
            suggestedReminderTime = LocalTime(7, 0)
        ),
        HabitTemplate(
            title = "Walk 10K Steps",
            description = "Daily movement for a healthier life",
            type = HabitType.GOOD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Count(10000, "steps"),
            reinforcementStyle = ReinforcementStyle.SOFT,
            motivationCopy = "One step at a time, you're building endurance.",
            recoveryCopy = "Even a short walk counts. Get moving.",
            suggestedReminderTime = LocalTime(8, 0)
        ),
        HabitTemplate(
            title = "Deep Work",
            description = "Focused, distraction-free work sessions",
            type = HabitType.GOOD,
            frequency = HabitFrequency.Scheduled(
                setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
            ),
            trackingType = TrackingType.Time(120),
            reinforcementStyle = ReinforcementStyle.AGGRESSIVE,
            motivationCopy = "Deep work is your competitive advantage.",
            recoveryCopy = "One bad day doesn't erase your focus. Get back.",
            suggestedReminderTime = LocalTime(9, 0)
        ),
        HabitTemplate(
            title = "Read",
            description = "Read at least 20 minutes daily",
            type = HabitType.GOOD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Time(20),
            reinforcementStyle = ReinforcementStyle.SOFT,
            motivationCopy = "Readers become leaders. Keep turning pages.",
            recoveryCopy = "Even one page is not zero. Open a book.",
            suggestedReminderTime = LocalTime(21, 0)
        ),
        HabitTemplate(
            title = "Meditate",
            description = "Daily mindfulness practice",
            type = HabitType.GOOD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Time(10),
            reinforcementStyle = ReinforcementStyle.SOFT,
            motivationCopy = "Stillness is strength. You're training your mind.",
            recoveryCopy = "One breath at a time. Sit down and begin.",
            suggestedReminderTime = LocalTime(6, 30)
        ),
        HabitTemplate(
            title = "Journal",
            description = "Reflect and write daily",
            type = HabitType.GOOD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Binary,
            reinforcementStyle = ReinforcementStyle.NEUTRAL,
            motivationCopy = "Writing clarifies thinking. Keep reflecting.",
            recoveryCopy = "Just one sentence counts. Write something.",
            suggestedReminderTime = LocalTime(22, 0)
        )
    )

    val badHabits: List<HabitTemplate> = listOf(
        HabitTemplate(
            title = "No Smoking",
            description = "Stay smoke-free today",
            type = HabitType.BAD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Avoidance,
            reinforcementStyle = ReinforcementStyle.AGGRESSIVE,
            motivationCopy = "You are becoming someone who doesn't need cigarettes.",
            recoveryCopy = "A slip doesn't erase your progress. Keep going.",
            suggestedReminderTime = LocalTime(8, 0)
        ),
        HabitTemplate(
            title = "No Doom Scrolling",
            description = "Avoid mindless social media browsing",
            type = HabitType.BAD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Avoidance,
            reinforcementStyle = ReinforcementStyle.NEUTRAL,
            motivationCopy = "Your attention is your most valuable asset.",
            recoveryCopy = "You caught yourself. That's awareness. Try again.",
            suggestedReminderTime = LocalTime(9, 0)
        ),
        HabitTemplate(
            title = "No Junk Food",
            description = "Avoid processed and junk food",
            type = HabitType.BAD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Avoidance,
            reinforcementStyle = ReinforcementStyle.SOFT,
            motivationCopy = "You're fueling your body with intention.",
            recoveryCopy = "One meal doesn't define your nutrition. Reset.",
            suggestedReminderTime = LocalTime(12, 0)
        ),
        HabitTemplate(
            title = "No Alcohol",
            description = "Stay sober and present",
            type = HabitType.BAD,
            frequency = HabitFrequency.Daily,
            trackingType = TrackingType.Avoidance,
            reinforcementStyle = ReinforcementStyle.AGGRESSIVE,
            motivationCopy = "Clarity over numbness. You're choosing control.",
            recoveryCopy = "One day doesn't define you. Today is a new start.",
            suggestedReminderTime = LocalTime(18, 0)
        )
    )

    val all: List<HabitTemplate> = goodHabits + badHabits
}
