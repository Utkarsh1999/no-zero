package com.nozero.shared.domain.usecase

import com.nozero.shared.domain.model.Habit
import com.nozero.shared.domain.model.HabitFrequency
import com.nozero.shared.domain.repository.HabitRepository
import com.nozero.shared.notification.NotificationScheduler
import kotlinx.datetime.DayOfWeek

/**
 * Creates and persists a new habit, and schedules notifications if needed.
 */
class CreateHabitUseCase(
    private val habitRepository: HabitRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(habit: Habit) {
        habitRepository.insertHabit(habit)
        
        habit.reminderTime?.let { time ->
            val days = when (val freq = habit.frequency) {
                is HabitFrequency.Daily -> DayOfWeek.entries.toSet()
                is HabitFrequency.TimesPerWeek -> DayOfWeek.entries.toSet()
                is HabitFrequency.Scheduled -> freq.days
            }
            notificationScheduler.scheduleHabitReminder(
                habitId = habit.id,
                title = "📌 ${habit.title}",
                message = "Time to work on your habit!",
                time = time,
                days = days,
                habitType = habit.type
            )
        }
    }
}
