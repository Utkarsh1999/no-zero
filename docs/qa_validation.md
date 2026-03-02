# QA & Validation Strategy: NoZero

## 1. Unit Testing Strategy
- **`shared:domain`**: 100% logic coverage for streak algorithms and consistency scoring.
- **`shared:motivation`**: Validation of message selection logic based on status.

## 2. Key Edge Cases
| Scenario | Goal |
| :--- | :--- |
| **Midnight Transition** | Ensure UI refreshes to next day without app restart. |
| **Leap Year** | Verify date logic doesn't crash or skip a day. |
| **Travel (Jump Forward)** | Handle timezones where a day is "skipped" in local time. |
| **Travel (Jump Backward)** | Prevent double-logging for the same calendar day. |

## 3. Manual Verification Flow
- **Habit Lifecycle**: Creation -> Daily Logging -> Milestone Achieved -> Identity Shift Messaging.
- **Configurability**: Check that reinforcement style changes (Soft/Aggressive) are reflected in the UI immediately.

## 4. Performance Benchmarks
- **Cold Start**: < 2 seconds.
- **Frame Rate**: Consistent 60fps on modern Android and iOS devices.
