# Domain Logic & Algorithms: NoZero

## 1. Streak Algorithm
The streak algorithm prioritizes consistency. It calculates the consecutive days of `COMPLETED` status. `EXEMPTED` days do not break the streak but "freeze" it.

### Pseudocode
```kotlin
fun calculateStreak(history: List<Completion>): Int {
    var streak = 0
    for (entry in history.sortedByDescending { it.date }) {
        when (entry.status) {
            COMPLETED -> streak++
            EXEMPTED -> continue
            MISSED, RELAPSED -> break
        }
    }
    return streak
}
```

## 2. Recovery Scoring (No-Zero Philosophy)
Instead of resetting to zero on a miss, the `ConsistencyScore` decays. This prevents the "What the Hell" effect and encourages immediate recovery.

### Recovery Engine Logic
- **Base Increment**: +0.1 for completion.
- **Miss Penalty**: -0.2 (Score is clamped at 0.0).
- **Recovery Bonus**: +0.15 if completing the day after a miss.
- **Exemption**: No change to score.

## 3. Core Models
- `Habit`: Represents a goal (Title, Type, Frequency, TrackingType).
- `Completion`: Represents a daily log (Date, Value, Status).
- `HabitFrequency`: Daily, Custom (X days/week), or Scheduled (Specific days).
