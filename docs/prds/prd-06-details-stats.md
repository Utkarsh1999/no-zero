# PRD-06: Habit Details & Stats

## 1. Objective
Provide users with a deep dive into their consistency patterns and historical resilience.

## 2. Requirements
- `HabitDetail` screen with historical calendar grid.
- Consistency metrics display (Best streak, average recovery).
- Habit editing/archiving capabilities.
- "Identity Path" visualization (Milestone progress).

## 3. Acceptance Criteria
- Calendar grid correctly shows completion history across months.
- Archiving a habit removes it from "Today" but preserves history.
- Milestone achievements are celebrated with minimalist UI effects.

## 4. Architect Review Notes
- **Approved**. The calendar grid component should be highly optimized for scrolling (LazyLayout if possible). Stats calculations should be cached.

## 5. Developer Task
- ✅ Built `HabitDetailViewModel` with 42-day calendar grid, Identity Path milestones, archiving.
- ✅ Built `HabitDetailScreen` with metrics cards, consistency grid, and milestone rows.
- ✅ Added habit tap navigation from `TodayScreen` → `HabitDetailScreen`.
- ✅ Updated `App.kt` NavHost with parameterized `habit_detail/{habitId}` route.
- ✅ Build verified: `assembleDebug` ✅, `testDebugUnitTest` ✅.

## 6. QA Checklist
- Verify history data matches across months.
- Check archive/unarchive behavior.
