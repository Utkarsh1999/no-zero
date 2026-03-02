# PRD-10: Platform Notifications & Reminders

## 1. Objective
Send scheduled local notifications to remind users about their habits at their preferred times. Fully platform-native implementation using `AlarmManager` on Android and `UNUserNotificationCenter` on iOS.

## 2. Current State
- `Habit.reminderTime: LocalTime?` already persisted in the DB (added in PRD-09)
- `HabitFrequency.Scheduled(days)` determines which days notifications should fire
- No notification permission requests, channels, or scheduling logic exists yet

## 3. Requirements

### 3.1 Architecture — Expect/Actual Pattern

```
commonMain/
  NotificationScheduler.kt    ← expect class

androidMain/
  NotificationScheduler.kt    ← actual (AlarmManager + BroadcastReceiver)

iosMain/
  NotificationScheduler.kt    ← actual (UNUserNotificationCenter)
```

### 3.2 Common Interface

```kotlin
expect class NotificationScheduler {
    fun scheduleHabitReminder(habitId: String, title: String, time: LocalTime, days: Set<DayOfWeek>)
    fun cancelHabitReminder(habitId: String)
    fun cancelAll()
    fun requestPermission(onResult: (Boolean) -> Unit)
}
```

### 3.3 Android Implementation

| Component | API |
|:---|:---|
| **Permission** | `POST_NOTIFICATIONS` (Android 13+), `SCHEDULE_EXACT_ALARM` (Android 12+) |
| **Scheduling** | `AlarmManager.setRepeating()` or `AlarmManager.setExactAndAllowWhileIdle()` |
| **Trigger** | `BroadcastReceiver` fires → builds `NotificationCompat.Builder` |
| **Channel** | `habit_reminders` channel, created at app startup |
| **Boot persistence** | `BOOT_COMPLETED` receiver re-schedules all active reminders |
| **Notification content** | Title: habit name, Body: motivational message |

### 3.4 iOS Implementation

| Component | API |
|:---|:---|
| **Permission** | `UNUserNotificationCenter.requestAuthorization(options: [.alert, .sound])` |
| **Scheduling** | `UNCalendarNotificationTrigger` with weekday + hour + minute |
| **Identifier** | `habit_{habitId}_{dayOfWeek}` for per-day scheduling |
| **Content** | Title: habit name, Body: motivational message |

### 3.5 When to Schedule
- On habit creation (if `reminderTime` is set)
- On habit edit (reschedule)
- On habit archive (cancel)
- On app launch (re-sync all — handles boot, updates, reinstalls)

### 3.6 Notification Behavior
- Tap notification → opens app to Today screen
- Notification appears at the exact `reminderTime` on scheduled days only
- Silent on non-scheduled days (no notification fired at all)

### 3.7 Settings Toggle
- Add "Notifications" toggle in a future Settings screen (out of scope for this PRD)
- For now, notifications are enabled by default if `reminderTime` is set

## 4. Acceptance Criteria
- [ ] Android: notification permission requested on first habit with time set
- [ ] Android: notification fires at correct time on correct days
- [ ] Android: boot receiver re-schedules all reminders
- [ ] Android: archiving a habit cancels its notifications
- [ ] iOS: permission requested via standard iOS dialog
- [ ] iOS: notification fires at correct time on correct days
- [ ] iOS: cancellation works on habit archive
- [ ] Both: creating a habit without `reminderTime` schedules nothing
- [ ] Both: tapping notification opens Today screen

## 5. Architect Review Notes
- **Approved**. The `expect/actual` pattern is the correct KMP approach.
- Android `AlarmManager` is preferred over `WorkManager` for exact-time reminders since we need minute-level precision.
- iOS should create one `UNNotificationRequest` per day-of-week per habit (max 7 per habit) for recurring weekly triggers.
- The boot receiver is critical on Android — alarms are lost on reboot.

> [!IMPORTANT]
> **Android 13+ requires runtime permission** for `POST_NOTIFICATIONS`. The app must handle the permission flow gracefully — request once, respect denial, and still function without notifications.

> [!WARNING]
> **Android 12+ exact alarm restriction**: `SCHEDULE_EXACT_ALARM` permission is needed. If denied, fall back to inexact alarms via `AlarmManager.setRepeating()` which may drift by up to 10 minutes.

## 6. Developer Task

### Android
- Register `habit_reminders` notification channel in `Application.onCreate()`
- Create `HabitReminderReceiver : BroadcastReceiver` that builds and shows the notification
- Create `BootReceiver : BroadcastReceiver` for `BOOT_COMPLETED` to re-schedule
- Implement `actual NotificationScheduler` using `AlarmManager`
- Add permissions to `AndroidManifest.xml`
- Request `POST_NOTIFICATIONS` permission at first schedule

### iOS
- Implement `actual NotificationScheduler` using `UNUserNotificationCenter`
- Create `UNCalendarNotificationTrigger` per day per habit
- Handle `requestAuthorization` result

### Common
- Call `NotificationScheduler.scheduleHabitReminder()` in `CreateHabitUseCase` when `reminderTime != null`
- Call `NotificationScheduler.cancelHabitReminder()` in archive flow
- Call re-sync on app startup

## 7. QA Checklist
- Set reminder for 2 minutes from now → verify notification fires
- Set Mon/Wed/Fri schedule → verify no notification on other days
- Archive habit → verify notification stops
- Reboot device (Android) → verify notification resumes
- Deny notification permission → verify app still works without crashing
- Create habit without time → verify no notification scheduled
