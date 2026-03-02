# PRD-11: Streak Forgiveness & Grace Days (Retention Hook)

## 1. Objective
Implement "Grace Days" (Streak Freezes) to prevent catastrophic user churn caused by accidentally breaking long-term streaks. This feature acts as the primary retention hook identified in user research.

## 2. The Hook Model
* **Cue:** User starts building a streak and gets invested in their `Consistency Grid`.
* **Action:** User logs their habit daily.
* **Reward:** User reaches a "perfect interval" (e.g., 14 consecutive days) and earns a "Grace Day". They feel a sense of security and achievement.
* **Investment:** Users accumulate Grace Days. If they miss a day due to life events (illness, travel), the app automatically consumes a Grace Day to "forgive" the miss, keeping the streak alive. The user's emotional investment in their streak is protected, preventing them from abandoning the app.

## 3. Core Mechanics

### 3.1 Earning Grace Days
* Every `X` consecutive days of perfect completion (e.g., 14 days) earns `1` Grace Day for that specific habit.
* A habit has an `earnedGraceDays` integer count in the database.
* Max grace days a habit can horde is capped (e.g., 3) to prevent abuse and keep the challenge meaningful.

### 3.2 Consuming Grace Days
* When the user misses a scheduled day, and the day passes (it's now "yesterday" or older), the app checks if `earnedGraceDays > 0`.
* If true, it consumes `1` Grace Day.
* The missed day's `CompletionStatus` is updated from `null` or `MISSED` to a new status: `FORGIVEN` (or `EXEMPTED`).
* A `FORGIVEN` day counts as a successful day when calculating streaks.
* The streak continues unbroken.

## 4. Database & Domain Models

### Data Schema Changes
1. **Habit Schema:** Add `earned_grace_days INTEGER NOT NULL DEFAULT 0`.
2. **CompletionStatus Enum:** Ensure a status like `FORGIVEN` exists (or we map it conceptually).
   * Currently, we have: `COMPLETED`, `RELAPSED` (for bad habits). We may just need to add a `skip` concept, or a specific `FORGIVEN` status.

### Domain Logic (CalculateConsistencyUseCase)
* The streak calculation logic strongly depends on consecutive completed days.
* `FORGIVEN` must be treated identically to `COMPLETED` for streak counting purposes.

## 5. UI Requirements

### 5.1 Today Screen
* No major changes. If a day is missed and forgiven, it happens in the background on the next day's launch.
* (Optional) Display a toast or notification on launch: "Your streak for [Habit Name] was saved by a Grace Day!"

### 5.2 Habit Detail Screen & Calendar Grid
* **Consistency Grid:** Needs a new color/treatment for `FORGIVEN` days. E.g., instead of vibrant green (Completed) or red/empty (Missed), it's a solid blue or gray checkmark, indicating a saved day.
* **Metrics Row:** Show available Grace Days. E.g., "Grace Days: 2/3".

## 6. Acceptance Criteria
- [ ] `Habit` model includes `earnedGraceDays`.
- [ ] Database migration adds `earned_grace_days` to the `habit` table.
- [ ] `CompletionStatus` supports a `FORGIVEN` (or `FROZEN`) state.
- [ ] Logic implemented to calculate earned grace days (every 14 day streak +1 grace day).
- [ ] Logic implemented to automatically consume a grace day when a required day is missed.
- [ ] `CalculateConsistencyUseCase` treats `FORGIVEN` days as streak-continuing.
- [ ] UI reflects `FORGIVEN` state in the calendar grid.
- [ ] UI displays current earned grace days.

## 7. QA Checklist
- Create a habit, set DB to have 14 days of history. Verify 1 grace day is earned.
- Simulate missing a day with 1 grace day available. Verify the day becomes FORGIVEN and streak continues.
- Simulate missing a day with 0 grace days available. Verify streak breaks.
- Verify max cap is respected (e.g., can't earn 10 grace days).
