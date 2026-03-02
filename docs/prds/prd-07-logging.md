# PRD-07: Multi-Type Habit Logging

## 1. Objective
Implement tracking-type-aware logging flows so users can record progress in a way that matches their habit's measurement type. Replace the current binary-only toggle with contextual logging UIs.

## 2. Current State
All habits use a single ✓/○ toggle regardless of `TrackingType`. The `Completion.value` field exists but is always `1.0`.

## 3. Requirements

### Logging Flows by TrackingType

| TrackingType | UI Interaction | Completion.value | CompletionStatus |
|:---|:---|:---|:---|
| `Binary` | Tap circle to toggle ✓/○ | `1.0` | `COMPLETED` / removed |
| `Time(targetMinutes)` | Tap → bottom sheet with minute input + quick-pick buttons (5, 15, 30, 60, 120) | Actual minutes logged | `COMPLETED` if ≥ target, `MISSED` if partial |
| `Count(target, unit)` | Tap → bottom sheet with numeric stepper + target label | Actual count logged | `COMPLETED` if ≥ target, `MISSED` if partial |
| `Avoidance` | Two-button card: "Clean ✓" / "I Slipped" | `1.0` (clean) or `0.0` (relapsed) | `COMPLETED` / `RELAPSED` |

### Habit Card UI Updates (Today Screen)
- **Binary**: Current ✓/○ circle (no change)
- **Time**: Show logged minutes / target (e.g., "45 / 120 min") + tap opens sheet
- **Count**: Show logged count / target (e.g., "6,200 / 10,000 steps") + tap opens sheet
- **Avoidance**: Replace toggle circle with two inline buttons

### Template & CreateHabit Integration
- Templates already define `TrackingType` — no change needed
- `CreateHabitScreen` already has a tracking type selector — the logging flow is automatically determined by the selected type
- When user selects `Time`, show an additional "Target Minutes" input
- When user selects `Count`, show "Target Amount" and "Unit" inputs

## 4. Acceptance Criteria
- [ ] Binary habits: tap-toggle works as before
- [ ] Time habits: bottom sheet opens, user enters minutes, saved as `Completion.value`
- [ ] Count habits: bottom sheet opens, user enters count, saved as `Completion.value`
- [ ] Avoidance habits: "Clean / Slipped" buttons shown, relapse saves `RELAPSED` status
- [ ] Habit card shows progress (e.g., "45/120 min") for non-binary types
- [ ] Partial completions (< target) are saved and count toward consistency
- [ ] CreateHabit form shows target input fields when Time or Count is selected

## 5. Architect Review Notes
- **Approved**. The `Completion.value` field was designed for this purpose. No schema changes needed.
- Partial completions should use `CompletionStatus.COMPLETED` with value < target (not `MISSED`) to honor the No-Zero philosophy — any effort counts.
- Bottom sheets should be dismissible without saving.

## 6. Developer Task
- Create `LoggingBottomSheet` composable (Time variant + Count variant)
- Update `HabitCard` to show tracking-type-aware UI
- Update `ToggleCompletionUseCase` to accept `value` and `status` parameters
- Add target input fields to `CreateHabitScreen` for Time/Count types
- Update `TodayViewModel` to pass tracking type context to the UI

## 7. QA Checklist
- Test each logging type end-to-end
- Verify partial values persist correctly in DB
- Verify avoidance relapse triggers `RELAPSED` status, not `MISSED`
- Test that calendar grid colors reflect partial vs full completions
- Verify bottom sheet dismissal doesn't create a completion record
