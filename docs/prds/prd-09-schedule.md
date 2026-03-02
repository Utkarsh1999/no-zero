# PRD-09: Day Schedule Support

## 1. Objective
Let users configure which days of the week each habit should be active. Display only scheduled habits on the Today screen. Lay the groundwork for future alarm/notification support by persisting preferred time slots.

## 2. Current State
- Domain model already has `HabitFrequency.Scheduled(days: Set<DayOfWeek>)` and `Habit.reminderTime: LocalTime?` — but **neither is exposed in the UI**
- All habits show every day regardless of frequency setting
- No way to pick specific weekdays during habit creation
- `CreateHabitScreen` only offers "Daily" and "3x per week" — no custom day picker

## 3. Requirements

### 3.1 Schedule Picker in CreateHabit / Add Habit
Add a weekday selector when creating or editing a habit:

| Frequency Option | UI | Stored As |
|:---|:---|:---|
| Every Day | Default, no day selector | `HabitFrequency.Daily` |
| Specific Days | 7-day pill selector (M T W T F S S) | `HabitFrequency.Scheduled(days)` |
| X Times per Week | Stepper (1–7) | `HabitFrequency.TimesPerWeek(count)` |

### 3.2 Time Slot Picker (Future Alarm Prep)
- Optional "Preferred Time" input below the day selector
- Uses a time picker (hour + minute) → stored as `Habit.reminderTime`
- **No alarm/notification functionality in this PRD** — just data capture
- Display label: "When do you plan to do this?" (not "Set Alarm")

### 3.3 Today Screen Filtering
- **Daily** habits: show every day
- **Scheduled** habits: show only on their scheduled weekdays
- **TimesPerWeek**: show every day (user decides when)
- Filtered in `GetActiveHabitsUseCase` or `TodayViewModel`

### 3.4 Habit Detail Schedule Display
- Show the habit's schedule on the detail screen (e.g., "Mon, Wed, Fri" or "Every Day")
- Show preferred time if set (e.g., "6:30 AM")

### 3.5 Template Updates
- Update existing templates to include sensible default schedules
- E.g., "Gym" → Mon/Wed/Fri, "Meditation" → Daily

## 4. Acceptance Criteria
- [ ] CreateHabit screen shows day picker when "Specific Days" is selected
- [ ] Optional time picker available for all frequency types
- [ ] Today screen hides habits not scheduled for today
- [ ] Habit detail screen displays schedule and preferred time
- [ ] Calendar grid accounts for non-scheduled days (don't mark as MISSED)
- [ ] Templates include default schedules

## 5. Architect Review Notes
- **Approved**. No schema changes needed — `frequencyType`/`frequencyValue` and `reminderTime` columns already exist.
- The Today screen filtering should happen at the ViewModel layer, not the SQL layer, to keep queries simple.
- Calendar grid: non-scheduled days should render as `EXEMPTED` (grey), not `MISSED` (red).

> [!IMPORTANT]
> This PRD explicitly does NOT include alarms/notifications. The `reminderTime` is saved for future use. Alarm support will be a separate PRD requiring platform-specific implementations (AlarmManager on Android, UNUserNotificationCenter on iOS).

## 6. Developer Task
- Add day picker composable (7 toggleable pills)
- Add time picker composable (hour:minute selector)
- Update `CreateHabitScreen` with new frequency options + time picker
- Update `TodayViewModel` to filter habits by today's day of week
- Update `HabitDetailScreen` to show schedule and time
- Update `HabitTemplates` with default schedules

## 7. QA Checklist
- Create a Mon/Wed/Fri habit → verify it hides on Tue/Thu/Sat/Sun
- Create a Daily habit → verify it shows every day
- Set a preferred time → verify it persists and displays on detail screen
- Verify calendar grid shows non-scheduled days as exempted
- Verify templates have correct default schedules
