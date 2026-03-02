# PRD-02: Domain Engine & Motivation

## 1. Objective
Implement the pure business logic for habit tracking, focusing on the "No-Zero" resilience philosophy and identity-based reinforcement.

## 2. Requirements
- Implementation of `StreakAlgorithm`.
- Implementation of `RecoveryEngine` for consistency scoring.
- Logic for `MilestoneDetection`.
- Identity-based reinforcement message selection logic.
- Definition of pure Kotlin `UseCases`.

## 3. Acceptance Criteria
- 100% unit test coverage for `StreakAlgorithm`.
- `ConsistencyScore` decays on miss but does not hit zero immediately.
- `RecoveryBonus` is correctly applied.
- Motivation messages correctly reflect habit type (Good/Bad).

## 4. Architect Review Notes
- **Approved**. The `ConsistencyScore` algorithm must be strictly pure Kotlin. Ensure leap years are handled in the `StreakAlgorithm` unit tests.

## 5. Developer Task
- Implement domain models and logic.
- Write extensive unit tests.

## 6. QA Checklist
- Verify edge cases (travel, midnight rollover logic in domain).
- Validate scoring under various history scenarios.
