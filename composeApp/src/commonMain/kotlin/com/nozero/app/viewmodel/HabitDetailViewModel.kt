package com.nozero.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nozero.shared.domain.model.*
import com.nozero.shared.domain.repository.CompletionRepository
import com.nozero.shared.domain.repository.HabitRepository
import com.nozero.shared.domain.usecase.CalculateConsistencyUseCase
import com.nozero.shared.motivation.MotivationEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class HabitDetailViewModel(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository,
    private val consistencyUseCase: CalculateConsistencyUseCase,
    private val motivationEngine: MotivationEngine,
    private val notificationScheduler: com.nozero.shared.notification.NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<HabitDetailUiState>(HabitDetailUiState.Loading)
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

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
                }
                delay(60_000)
            }
        }
    }

    fun loadHabit(habitId: String) {
        // Reset state immediately to avoid showing stale data from a previous habit
        _uiState.value = HabitDetailUiState.Loading

        viewModelScope.launch {
            try {
                combine(
                    currentDateFlow,
                    habitRepository.getHabitById(habitId),
                    completionRepository.getCompletionsForHabit(habitId)
                ) { currentDate, habit, history ->
                    Triple(currentDate, habit, history)
                }.collect { triple ->
                    val currentDate = triple.first
                    val habit = triple.second
                    val history = triple.third

                    if (habit == null) {
                        _uiState.value = HabitDetailUiState.Error("Habit not found")
                        return@collect
                    }

                    val metrics = consistencyUseCase.execute(history)
                    val calendarDays = buildCalendarGrid(history, currentDate)
                    val milestones = buildMilestones(metrics)

                    val msg = motivationEngine.getDailyMessage(
                        habitTitle = habit.title,
                        habitType = habit.type,
                        metrics = metrics,
                        style = habit.reinforcementStyle
                    )

                    _uiState.value = HabitDetailUiState.Success(
                        habit = habit,
                        metrics = metrics,
                        calendarDays = calendarDays,
                        milestones = milestones,
                        motivationMessage = msg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HabitDetailUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun archiveHabit(habitId: String) {
        viewModelScope.launch {
            habitRepository.archiveHabit(habitId)
            notificationScheduler.cancelHabitReminder(habitId)
        }
    }

    private fun buildCalendarGrid(history: List<Completion>, evalDate: LocalDate): List<DayCell> {
        val statusMap = history.associate { it.date to it.status }
        val days = mutableListOf<DayCell>()

        // Start from the Monday 5 weeks ago so the grid aligns with M-T-W-T-F-S-S headers
        val rawStart = evalDate.minus(DatePeriod(days = 41))
        val daysSinceMonday = (rawStart.dayOfWeek.ordinal) // Monday=0, Sunday=6
        val alignedStart = rawStart.minus(DatePeriod(days = daysSinceMonday))

        var current = alignedStart
        while (current <= evalDate) {
            days.add(
                DayCell(
                    date = current,
                    status = statusMap[current],
                    isToday = current == evalDate,
                    isFuture = false
                )
            )
            current = current.plus(DatePeriod(days = 1))
        }
        // Fill remaining days to complete the week
        val remainingInWeek = 7 - (days.size % 7)
        if (remainingInWeek < 7) {
            repeat(remainingInWeek) { i ->
                val futureDate = evalDate.plus(DatePeriod(days = i + 1))
                days.add(DayCell(date = futureDate, status = null, isToday = false, isFuture = true))
            }
        }
        return days
    }

    private fun buildMilestones(metrics: ConsistencyMetrics): List<MilestoneUiModel> {
        return listOf(
            MilestoneUiModel(
                title = "The Spark",
                description = "3-day streak",
                isAchieved = metrics.bestStreak >= 3
            ),
            MilestoneUiModel(
                title = "The Reliable",
                description = "14-day streak",
                isAchieved = metrics.bestStreak >= 14
            ),
            MilestoneUiModel(
                title = "The Architect",
                description = "30-day streak",
                isAchieved = metrics.bestStreak >= 30
            ),
            MilestoneUiModel(
                title = "The Unstoppable",
                description = "60-day streak",
                isAchieved = metrics.bestStreak >= 60
            ),
            MilestoneUiModel(
                title = "The Legend",
                description = "100-day streak",
                isAchieved = metrics.bestStreak >= 100
            )
        )
    }
}
