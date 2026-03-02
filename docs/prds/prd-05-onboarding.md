# PRD-05: Onboarding & Habit Templates

## 1. Objective
Create a seamless onboarding experience that allows users to pick from templates or create custom habits quickly.

## 2. Requirements
- Onboarding flow explaining the "No-Zero" philosophy.
- Template selection screen (Good vs. Bad habits).
- Custom habit creation form.
- Frequency configuration (Daily, Weekly, Scheduled).

## 3. Acceptance Criteria
- User can go from first launch to first habit in < 60 seconds.
- Templates prepopulate reinforcement copy and frequency.
- Validation on form fields (Title required).

## 4. Architect Review Notes
- **Approved**. Onboarding state must be persisted in a shared preference/user default wrapper to avoid repetition.

## 5. Developer Task
- ✅ Implemented `AppPreferences` (expect/actual for Android/iOS).
- ✅ Created `HabitTemplate` with 10 pre-defined templates (6 Good, 4 Bad).
- ✅ Built `OnboardingScreen` (3-page animated intro).
- ✅ Built `TemplateSelectionScreen` (BUILD/AVOID tabs, multi-select).
- ✅ Built `CreateHabitScreen` (validated form with type/frequency/tracking).
- ✅ Created `OnboardingViewModel` with persistence and habit creation logic.
- ✅ Updated `App.kt` with full NavHost navigation graph.
- ✅ Build verified: `assembleDebug` ✅, `testDebugUnitTest` ✅.

## 6. QA Checklist
- Test template selection accuracy.
- Verify onboarding isn't shown twice.
