# Roadmap & Risks: NoZero

## 1. 30-Day Product Roadmap

### Week 1: Foundation
- Project initialization (KMP shared module).
- SQLDelight setup and drivers.
- CI/CD basic pipeline.

### Week 2: Logic & Engine
- Domain models and streak algorithm implementation.
- Motivation engine and message selectors.
- Unit testing core logic.

### Week 3: Persistence & Data
- Repository implementations.
- SQLite database migrations.
- Template application logic.

### Week 4: UI & Polishing
- Dashboard and Habit Detail screens.
- Navigation graph integration.
- Final QA and performance profiling.

## 2. Risk Analysis
- **Timezone Drift**: Handled by using `LocalDate` relative to device time.
- **Data Loss**: Prevented by persistent SQLite storage; cloud sync is a future milestone.
- **UI Performance**: Monitored via Compose multiplatform stability markers.

## 3. Future Extensibility
- **Cloud Sync**: Supabase/Ktor integration.
- **Social Circles**: Private accountability groups.
- **Wearables**: Apple Watch & Wear OS companion apps.
