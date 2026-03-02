# PRD-08: In-App Guided Tutorial

## 1. Objective
Provide a one-time interactive guided tutorial on first app launch (after onboarding) that teaches users how to use the core features: logging habits, navigating to details, and understanding the consistency grid.

## 2. Requirements

### Tutorial Flow
The tutorial runs **once** on the first launch after onboarding completes. It uses a coach-mark spotlight overlay system that steps through the live UI.

| Step | Spotlight Target | Message |
|:--|:---|:---|
| 1 | First habit card | "This is your habit card. It shows your streak and today's status." |
| 2 | Toggle / Log button | "Tap here to log your progress. Different habit types have different logging methods." |
| 3 | Habit card body | "Tap the card to see your full history, calendar grid, and identity milestones." |
| 4 | FAB (+ button) | "Need more habits? Tap here to add from templates or create your own." |
| 5 | Done overlay | "You're all set. Remember: no zero days. 🔥" |

### Technical Approach
- **Coach-mark overlay**: Semi-transparent dark background with a spotlight cutout around the target element, tooltip message, and "Next" / "Got it" button.
- **No video file needed** — the tutorial runs directly on the live Today screen UI using Compose overlays. This is more performant and doesn't require a video player.
- **State persisted** via `AppPreferences` (`tutorial_completed` key) so it only shows once.
- **Skippable** — "Skip Tutorial" link always visible.

### When to Show
- After onboarding is completed **and** at least 1 habit exists
- If the user skips onboarding and has no habits, show tutorial after they create their first habit

## 3. Acceptance Criteria
- [ ] Tutorial appears exactly once after first onboarding completion
- [ ] Each step highlights the correct UI element with a spotlight cutout
- [ ] User can skip at any step
- [ ] Completing or skipping persists state — never shows again
- [ ] Tutorial works correctly on different screen sizes
- [ ] If 0 habits exist, tutorial is deferred until first habit is created

## 4. Architect Review Notes
- **Approved with note**: A Compose overlay system is preferred over embedding a video file for these reasons:
  - Works cross-platform (Android + iOS) without platform-specific video players
  - Teaches users on their **actual UI** with real data — more effective than a generic video
  - Smaller app size (no video asset bundled)
  - Easier to maintain when UI changes

## 5. Developer Task
- Create `TutorialOverlay` composable with spotlight cutout rendering
- Create `TutorialStep` data class and step sequence
- Add `tutorial_completed` preference key
- Integrate overlay into `TodayScreen` after onboarding
- Handle edge case: defer until first habit exists

## 6. QA Checklist
- Verify tutorial shows once only
- Verify skip works at each step
- Verify spotlight aligns correctly on different devices
- Clear app data → verify tutorial appears again
- Verify tutorial doesn't show if onboarding not completed
