package com.nozero.shared.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nozero.shared.domain.model.HabitFrequency
import com.nozero.shared.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Re-schedules all habit reminders after device reboot.
 * Alarms set via AlarmManager are lost on reboot.
 */
class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val habitRepository: HabitRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val scheduler = NotificationScheduler(context)

        CoroutineScope(Dispatchers.IO).launch {
            val habits = habitRepository.getActiveHabits().first()
            habits.forEach { habit ->
                val time = habit.reminderTime ?: return@forEach
                val days = when (habit.frequency) {
                    is HabitFrequency.Daily -> kotlinx.datetime.DayOfWeek.entries.toSet()
                    is HabitFrequency.Scheduled ->
                        (habit.frequency as HabitFrequency.Scheduled).days
                    is HabitFrequency.TimesPerWeek -> kotlinx.datetime.DayOfWeek.entries.toSet()
                }
                scheduler.scheduleHabitReminder(
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
}
