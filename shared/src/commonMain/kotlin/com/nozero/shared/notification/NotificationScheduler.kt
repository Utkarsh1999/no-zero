package com.nozero.shared.notification

import com.nozero.shared.domain.model.HabitType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

/**
 * Platform-specific notification scheduler.
 * Android: AlarmManager + BroadcastReceiver
 * iOS: UNUserNotificationCenter
 */
expect class NotificationScheduler {
    /**
     * Schedule recurring reminders for a habit at the given time on the given days.
     */
    fun scheduleHabitReminder(
        habitId: String,
        title: String,
        message: String,
        time: LocalTime,
        days: Set<DayOfWeek>,
        habitType: HabitType
    )

    /**
     * Cancel all reminders for a specific habit.
     */
    fun cancelHabitReminder(habitId: String)

    /**
     * Cancel all scheduled notifications.
     */
    fun cancelAll()
}
