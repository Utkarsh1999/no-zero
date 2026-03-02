package com.nozero.shared.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat

/**
 * BroadcastReceiver that fires when an alarm triggers.
 * Builds and shows the habit reminder notification.
 */
class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val habitId = intent.getStringExtra(NotificationScheduler.EXTRA_HABIT_ID) ?: return
        val title = intent.getStringExtra(NotificationScheduler.EXTRA_HABIT_TITLE) ?: "Habit Reminder"
        val message = intent.getStringExtra(NotificationScheduler.EXTRA_HABIT_MESSAGE) ?: "Time to work on your habit!"
        val habitTypeStr = intent.getStringExtra(NotificationScheduler.EXTRA_HABIT_TYPE)

        val notificationId = habitId.hashCode()

        // Dynamically resolve resources since this is in the `shared` module, but the drawables are in `composeApp`
        val smallIconResId = context.resources.getIdentifier(
            if (habitTypeStr == "BAD") "ic_habit_bad" else "ic_habit_good",
            "drawable",
            context.packageName
        )
        val finalSmallIcon = if (smallIconResId != 0) smallIconResId else android.R.drawable.ic_dialog_info

        val actionIconResId = context.resources.getIdentifier(
            "ic_action_check", "drawable", context.packageName
        )
        val finalActionIcon = if (actionIconResId != 0) actionIconResId else android.R.drawable.ic_input_add

        val actionIntent = Intent(context, HabitActionReceiver::class.java).apply {
            putExtra(NotificationScheduler.EXTRA_HABIT_ID, habitId)
        }
        val actionPendingIntent = android.app.PendingIntent.getBroadcast(
            context,
            habitId.hashCode(),
            actionIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val action = NotificationCompat.Action.Builder(
            finalActionIcon, "Log Habit", actionPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(finalSmallIcon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(action)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Ensure channel exists because if app was killed, NotificationScheduler might not have run its init block
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationScheduler.CHANNEL_ID,
                NotificationScheduler.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for your daily habits"
            }
            manager.createNotificationChannel(channel)
        }

        manager.notify(notificationId, notification)
    }
}
