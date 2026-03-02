# PRD-12: Rich & Actionable Notifications

## 1. Objective
Enhance the current notification system to be visually engaging and highly actionable. By allowing users to log habits directly from their lock screen or notification shade, we reduce friction to zero, drastically improving tracking consistency. Furthermore, varied and thematic icons will make the app feel more premium and personalized.

## 2. Key Features

### 2.1 Actionable Notifications (Inline Logging)
- **"Log Habit" Action:** Notifications will include a primary action button (e.g., "Done!", "Completed", or "+1").
- **Background Execution:** Tapping the action button should silently update the database in the background without launching the full application UI.
- **Dismissal:** Upon successfully logging the habit via the action button, the notification should be cleared automatically.
- **Support for Binary Habits:** This inline logging is perfect for `TrackingType.Binary`. *Note: For `Count` or `Time` based habits, the action might be "Add 1" or launch the app to the specific habit screen depending on technical feasibility.*

### 2.2 Dynamic Custom Icons
- **Habit-Specific Icons:** Instead of a generic app logo or an `ic_dialog_info` icon for every notification, map different icons based on:
  - `HabitType` (Good vs. Bad)
  - Selected template categories (e.g., a dumbbell for fitness, a book for reading, water drops for hydration).
- **Large Icon Support:** Use rich `LargeIcon` capabilities on Android to display a colorful emblem alongside the text.

## 3. Platform Specifics

### Android Implementation
- **Actions:** Use `NotificationCompat.Action.Builder` with a `PendingIntent`.
- **Receiver:** Create a new `BroadcastReceiver` (e.g., `HabitActionReceiver`) specifically designed to intercept the "Log Habit" intent.
  - This receiver must inject Koin dependencies (like `ToggleCompletionUseCase` or `CompletionRepository`).
  - It will run in `Dispatchers.IO` to insert the completion record.
- **Icons:** We will need to bundle several vector drawables into `composeApp/src/androidMain/res/drawable/`.

### iOS Implementation (Context)
- **Categories & Actions:** iOS requires registering `UNNotificationCategory` with `UNNotificationAction` objects at app launch.
- **Handling Actions:** Intercepting iOS notification actions in the background requires implementing `UNUserNotificationCenterDelegate`'s `didReceiveNotificationResponse` method. *Since our iOS app logic is largely in Kotlin, handling background data writes precisely when an action is tapped involves specific Kotlin/Native interop, which might be complex. We will prioritize Android first if iOS background execution proves difficult.*

## 4. UI/UX Changes
- **Habit Creation/Editing (Future Enhancement):** Let users pick an icon/emoji for their habit, which is then passed down to the `NotificationScheduler`.
- **Notification Shade:** Users see:
  - Title: "📌 Morning Run"
  - Body: "Time to hit the pavement!"
  - Actions: [ Done ] [ Snooze ]

## 5. Acceptance Criteria
- [ ] Notifications display a dynamic icon based on the habit's configuration or type.
- [ ] Notifications include a "Log Habit" or "Done" action button.
- [ ] Tapping the action button successfully records a `COMPLETED` entry for the habit on the current date.
- [ ] The notification clears itself after the action is processed.
- [ ] The main app UI (Today Screen / Detail Screen) reflects the completion if opened afterward.
