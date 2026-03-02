package com.nozero.shared.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nozero.shared.domain.model.HabitType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import java.util.Calendar

actual class NotificationScheduler(
    private val context: Context
) {
    companion object {
        const val CHANNEL_ID = "habit_reminders"
        const val CHANNEL_NAME = "Habit Reminders"
        const val EXTRA_HABIT_ID = "habit_id"
        const val EXTRA_HABIT_TITLE = "habit_title"
        const val EXTRA_HABIT_MESSAGE = "habit_message"
        const val EXTRA_HABIT_TYPE = "habit_type"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for your daily habits"
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    actual fun scheduleHabitReminder(
        habitId: String,
        title: String,
        message: String,
        time: LocalTime,
        days: Set<DayOfWeek>,
        habitType: HabitType
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule one alarm per day of week
        days.forEach { dayOfWeek ->
            val requestCode = generateRequestCode(habitId, dayOfWeek)

            val intent = Intent(context, HabitReminderReceiver::class.java).apply {
                putExtra(EXTRA_HABIT_ID, habitId)
                putExtra(EXTRA_HABIT_TITLE, title)
                putExtra(EXTRA_HABIT_MESSAGE, message)
                putExtra(EXTRA_HABIT_TYPE, habitType.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Calculate next trigger time for this day of week
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, time.hour)
                set(Calendar.MINUTE, time.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                set(Calendar.DAY_OF_WEEK, toCalendarDay(dayOfWeek))

                // If the time has already passed this week, move to next week
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }

            // Schedule repeating weekly alarm
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY * 7, // Weekly repeat
                pendingIntent
            )
        }
    }

    actual fun cancelHabitReminder(habitId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel all 7 possible day alarms for this habit
        DayOfWeek.entries.forEach { dayOfWeek ->
            val requestCode = generateRequestCode(habitId, dayOfWeek)
            val intent = Intent(context, HabitReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }

    actual fun cancelAll() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()
    }

    private fun generateRequestCode(habitId: String, dayOfWeek: DayOfWeek): Int {
        return (habitId.hashCode() * 10) + dayOfWeek.ordinal
    }

    private fun toCalendarDay(day: DayOfWeek): Int = when (day) {
        DayOfWeek.MONDAY -> Calendar.MONDAY
        DayOfWeek.TUESDAY -> Calendar.TUESDAY
        DayOfWeek.WEDNESDAY -> Calendar.WEDNESDAY
        DayOfWeek.THURSDAY -> Calendar.THURSDAY
        DayOfWeek.FRIDAY -> Calendar.FRIDAY
        DayOfWeek.SATURDAY -> Calendar.SATURDAY
        DayOfWeek.SUNDAY -> Calendar.SUNDAY
        else -> Calendar.MONDAY
    }
}
