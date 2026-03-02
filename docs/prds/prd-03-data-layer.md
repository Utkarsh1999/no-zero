# PRD-03: Data Layer & Persistence

## 1. Objective
Implement the local-first storage system and repository patterns to provide a reliable source of truth.

## 2. Requirements
- Implementation of `HabitRepository` and `CompletionRepository`.
- SQLDelight DAO implementations.
- Data mapping between DB Entities and Domain Models.
- Implementation of soft-delete and sync-ready flags.

## 3. Acceptance Criteria
- Data persists across app restarts.
- `ON DELETE CASCADE` correctly removes completions when a habit is deleted.
- Flows from the repository emit new data whenever the DB changes.

## 4. Architect Review Notes
- **Approved**. Use `Flow` as the default return type for all observable data. Ensure `isDeleted` flag is handled in all `get` queries.

## 5. Developer Task
- Implement repositories and mappers.
- Write integration tests for the DB layer.

## 6. QA Checklist
- Verify data integrity on concurrent writes.
- Test database migrations (dummy migration).
