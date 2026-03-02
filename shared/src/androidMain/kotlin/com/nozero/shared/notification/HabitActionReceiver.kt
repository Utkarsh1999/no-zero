package com.nozero.shared.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nozero.shared.domain.usecase.ToggleCompletionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Handles the "Log Habit" action from a notification, processing the completion
 * silently in the background without opening the app UI.
 */
class HabitActionReceiver : BroadcastReceiver(), KoinComponent {

    private val toggleCompletion: ToggleCompletionUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra(NotificationScheduler.EXTRA_HABIT_ID) ?: return
        val notificationId = habitId.hashCode()

        // Hide notification immediately for a responsive feel
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(notificationId)

        // Log the habit in the background
        CoroutineScope(Dispatchers.IO).launch {
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            try {
                toggleCompletion(habitId, today, currentlyCompleted = false)
            } catch (e: Exception) {
                // Ignore or log error
            }
        }
    }
}
