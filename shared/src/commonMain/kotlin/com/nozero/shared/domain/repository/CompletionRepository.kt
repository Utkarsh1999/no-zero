package com.nozero.shared.domain.repository

import com.nozero.shared.domain.model.Completion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface CompletionRepository {
    fun getCompletionsForHabit(habitId: String): Flow<List<Completion>>
    fun getCompletionsForDate(date: LocalDate): Flow<List<Completion>>
    fun getCompletionRange(habitId: String, from: LocalDate, to: LocalDate): Flow<List<Completion>>
    suspend fun recordCompletion(completion: Completion)
    suspend fun deleteCompletion(habitId: String, date: LocalDate)
}
