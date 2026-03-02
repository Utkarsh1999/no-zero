# Data Layer & Persistence: NoZero

## 1. Database Schema (SQLDelight)

### `HabitEntity`
- `id`: UUID Primary Key.
- `title`: String.
- `type`: `GOOD`, `BAD`.
- `frequency`: JSON-serialized structure.
- `trackingType`: `BINARY`, `AVOIDANCE`, `COUNT`, `TIME`.
- `isArchived`: Boolean.

### `CompletionEntity`
- `habitId`: Foreign Key to `HabitEntity`.
- `date`: ISO-8601 String (`YYYY-MM-DD`).
- `status`: `COMPLETED`, `MISSED`, `EXEMPTED`, `RELAPSED`.

## 2. Repository Pattern
All data access is mediated by repositories in the `shared:domain` layer, implemented in `shared:data`.

- `HabitRepository`: CRUD operations for habits.
- `CompletionRepository`: History tracking and daily logging.

## 3. Offline-First & Sync
The app uses SQLite via SQLDelight as the single source of truth. All entities include `lastModifiedAt` and `isDeleted` flags to support future eventual consistency and synchronization with a remote backend (Supabase/Ktor).
