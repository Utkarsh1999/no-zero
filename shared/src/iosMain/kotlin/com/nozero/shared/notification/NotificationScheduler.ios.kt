package com.nozero.shared.notification

import com.nozero.shared.domain.model.HabitType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import platform.UserNotifications.*

actual class NotificationScheduler {

    actual fun scheduleHabitReminder(
        habitId: String,
        title: String,
        message: String,
        time: LocalTime,
        days: Set<DayOfWeek>,
        habitType: HabitType
    ) {
        val center = UNUserNotificationCenter.currentNotificationCenter()

        // Request permission first
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound
        ) { granted, _ ->
            if (!granted) return@requestAuthorizationWithOptions

            // Schedule one trigger per day
            days.forEach { dayOfWeek ->
                val content = UNMutableNotificationContent().apply {
                    setTitle(title)
                    setBody(message)
                    setSound(UNNotificationSound.defaultSound())
                }

                val dateComponents = NSDateComponents().apply {
                    setHour(time.hour.toLong())
                    setMinute(time.minute.toLong())
                    // iOS weekdays: 1 = Sunday, 2 = Monday, ..., 7 = Saturday
                    setWeekday(toIosWeekday(dayOfWeek).toLong())
                }

                val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                    dateComponents,
                    repeats = true
                )

                val identifier = "habit_${habitId}_${dayOfWeek.name}"
                val request = UNNotificationRequest.requestWithIdentifier(
                    identifier,
                    content,
                    trigger
                )

                center.addNotificationRequest(request) { _ -> }
            }
        }
    }

    actual fun cancelHabitReminder(habitId: String) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val identifiers = DayOfWeek.entries.map { "habit_${habitId}_${it.name}" }
        center.removePendingNotificationRequestsWithIdentifiers(identifiers)
    }

    actual fun cancelAll() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.removeAllPendingNotificationRequests()
    }

    private fun toIosWeekday(day: DayOfWeek): Int = when (day) {
        DayOfWeek.SUNDAY -> 1
        DayOfWeek.MONDAY -> 2
        DayOfWeek.TUESDAY -> 3
        DayOfWeek.WEDNESDAY -> 4
        DayOfWeek.THURSDAY -> 5
        DayOfWeek.FRIDAY -> 6
        DayOfWeek.SATURDAY -> 7
        else -> 2
    }
}
