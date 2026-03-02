package com.nozero.shared.domain.usecase

import com.nozero.shared.domain.model.Completion
import com.nozero.shared.domain.model.CompletionStatus
import com.nozero.shared.domain.repository.CompletionRepository
import com.nozero.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

/**
 * Evaluates the history of all active habits up to 'yesterday'.
 * Detects missed days and consumes earned grace days to insert EXEMPTED records.
 * If no grace days are available, inserts a MISSED record.
 * This fixes the missing logic where CalculateConsistencyUseCase needs explicit MISSED states.
 */
class ProcessGraceDaysUseCase(
    private val habitRepository: HabitRepository,
    private val completionRepository: CompletionRepository
) {
    suspend operator fun invoke() {
        val habits = habitRepository.getActiveHabits().first()
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        
        for (habit in habits) {
            val history = completionRepository.getCompletionsForHabit(habit.id).first()
            val historyMap = history.associateBy { it.date }
            
            val start = habit.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
            var current = start
            var earnedGraceDays = habit.earnedGraceDays
            var habitModified = false
            
            while (current < today) {
                val completion = historyMap[current]
                val isScheduled = habit.frequency.isScheduledFor(current.dayOfWeek)
                
                // If it's a scheduled day but there is no completion record, they missed it.
                if (isScheduled && completion == null) {
                    if (earnedGraceDays > 0) {
                        earnedGraceDays--
                        habitModified = true
                        
                        val exemptedCompletion = Completion(
                            habitId = habit.id,
                            date = current,
                            status = CompletionStatus.EXEMPTED
                        )
                        completionRepository.recordCompletion(exemptedCompletion)
                    } else {
                        // Mark as missed explicitly so consistency calculates correctly
                        val missedCompletion = Completion(
                            habitId = habit.id,
                            date = current,
                            status = CompletionStatus.MISSED
                        )
                        completionRepository.recordCompletion(missedCompletion)
                    }
                }
                current = current.plus(DatePeriod(days = 1))
            }
            
            if (habitModified) {
                habitRepository.updateHabit(habit.copy(earnedGraceDays = earnedGraceDays))
            }
        }
    }
}
