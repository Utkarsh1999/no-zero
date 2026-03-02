# Presentation Layer & UI Design: NoZero

## 1. UI Philosophy: 90% Whitespace
The interface is designed to be extremely minimalist, reducing cognitive load and focusing the user on a single primary action per screen.

### Design Principles
- **Typography-Focused**: Text is used to establish hierarchy, not boxes or borders.
- **Dark Mode First**: Optimized for OLED screens with deep blacks (#000000) and soft-white text (#F5F5F5).
- **Micro-Animations**: Uses non-linear spring damping for smooth transitions.

## 2. Navigation Graph
- **`Onboarding`**: Introduction and initial habit selection.
- **`Today`**: The primary daily view with habit cards toggles.
- **`HabitDetail`**: In-depth metrics, history grid, and settings for a specific habit.
- **`Stats`**: Global consistency overview.
- **`Settings`**: App-wide preferences.

## 3. UI State Management (UDF)
The application follows a Unidirectional Data Flow. Each screen has a dedicated `UiState` model populated by a ViewModel.

### Example: `TodayUiState`
```kotlin
data class TodayUiState(
    val habits: List<HabitItemUiModel>,
    val overallConsistency: Double,
    val dailyMotivation: String
)
```
