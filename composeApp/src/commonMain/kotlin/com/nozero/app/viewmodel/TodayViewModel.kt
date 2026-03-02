package com.nozero.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nozero.shared.domain.model.CompletionStatus
import com.nozero.shared.domain.model.ConsistencyMetrics
import com.nozero.shared.domain.model.ReinforcementStyle
import com.nozero.shared.domain.model.TrackingType
import com.nozero.shared.domain.repository.CompletionRepository
import com.nozero.shared.domain.usecase.CalculateConsistencyUseCase
import com.nozero.shared.domain.usecase.GetActiveHabitsUseCase
import com.nozero.shared.domain.usecase.ProcessGraceDaysUseCase
import com.nozero.shared.domain.usecase.ToggleCompletionUseCase
import com.nozero.shared.motivation.MotivationEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.*

class TodayViewModel(
    private val getActiveHabits: GetActiveHabitsUseCase,
    private val toggleCompletion: ToggleCompletionUseCase,
    private val processGraceDays: ProcessGraceDaysUseCase,
    private val completionRepository: CompletionRepository,
    private val consistencyUseCase: CalculateConsistencyUseCase,
    private val motivationEngine: MotivationEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<TodayUiState>(TodayUiState.Loading)
    val uiState: StateFlow<TodayUiState> = _uiState.asStateFlow()

    private val currentDateFlow = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )

    private val today: LocalDate
        get() = currentDateFlow.value

    init {
        viewModelScope.launch {
            while (true) {
                val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                if (current != currentDateFlow.value) {
                    currentDateFlow.value = current
                    // Also run grace day processing when day actually rolls over
                    processGraceDays()
                }
                delay(60_000) // check every minute
            }
        }
        viewModelScope.launch {
            processGraceDays()
            loadHabits()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun loadHabits() {
        try {
            currentDateFlow.flatMapLatest { currentDate ->
                combine(
                    getActiveHabits(),
                    completionRepository.getCompletionsForDate(currentDate)
                ) { habits, todayCompletions ->
                    Triple(habits, todayCompletions, currentDate)
                }
            }.collect { triple ->
                val habits = triple.first
                val todayCompletions = triple.second
                val evalDate = triple.third

                val completionMap = todayCompletions.associateBy { it.habitId }
                val todayDayOfWeek = evalDate.dayOfWeek

                // Filter habits by schedule — only show habits due today
                    val scheduledHabits = habits.filter { habit ->
                        when (habit.frequency) {
                            is com.nozero.shared.domain.model.HabitFrequency.Daily -> true
                            is com.nozero.shared.domain.model.HabitFrequency.TimesPerWeek -> true
                            is com.nozero.shared.domain.model.HabitFrequency.Scheduled ->
                                todayDayOfWeek in (habit.frequency as com.nozero.shared.domain.model.HabitFrequency.Scheduled).days
                        }
                    }

                    val items = scheduledHabits.map { habit ->
                        val todayCompletion = completionMap[habit.id]
                        val isCompleted = todayCompletion?.status == CompletionStatus.COMPLETED ||
                                todayCompletion?.status == CompletionStatus.RELAPSED

                        val history = completionRepository
                            .getCompletionsForHabit(habit.id)
                            .first()
                        val metrics = consistencyUseCase.execute(history)

                        val msg = motivationEngine.getDailyMessage(
                            habitTitle = habit.title,
                            habitType = habit.type,
                            metrics = metrics,
                            style = habit.reinforcementStyle
                        )

                        HabitItemUiModel(
                            id = habit.id,
                            title = habit.title,
                            type = habit.type,
                            trackingType = habit.trackingType,
                            currentStreak = metrics.currentStreak,
                            consistencyScore = metrics.consistencyScore,
                            isCompletedToday = isCompleted,
                            loggedValue = todayCompletion?.value ?: 0.0,
                            motivationMessage = msg
                        )
                    }

                    val globalMsg = if (items.isEmpty()) {
                        "Start your journey. Add your first habit."
                    } else if (items.all { it.isCompletedToday }) {
                        "All done for today. You showed up. 💪"
                    } else {
                        "Every action is a vote for who you want to become."
                    }

                    _uiState.value = TodayUiState.Success(
                        dateLabel = formatDate(evalDate),
                        habits = items,
                        dailyMotivation = globalMsg
                    )
                }
        } catch (e: Exception) {
            _uiState.value = TodayUiState.Error(e.message ?: "Something went wrong")
        }
    }

    /** Binary toggle (for TrackingType.Binary) */
    fun onToggleCompletion(habitId: String, currentlyCompleted: Boolean) {
        viewModelScope.launch {
            toggleCompletion(habitId, today, currentlyCompleted)
        }
    }

    /** Log a value (for TrackingType.Time / Count) */
    fun onLogValue(habitId: String, value: Double) {
        viewModelScope.launch {
            toggleCompletion.logValue(
                habitId = habitId,
                date = today,
                value = value,
                status = CompletionStatus.COMPLETED
            )
        }
    }

    /** Log avoidance result (for TrackingType.Avoidance) */
    fun onLogAvoidance(habitId: String, stayed: Boolean) {
        viewModelScope.launch {
            toggleCompletion.logValue(
                habitId = habitId,
                date = today,
                value = if (stayed) 1.0 else 0.0,
                status = if (stayed) CompletionStatus.COMPLETED else CompletionStatus.RELAPSED
            )
        }
    }

    private fun formatDate(date: LocalDate): String {
        val month = date.month.name.lowercase().replaceFirstChar { it.uppercase() }.take(3)
        return "${date.dayOfMonth} $month ${date.year}"
    }
}
