package com.nozero.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.nozero.shared.data.local.NoZeroDatabase
import com.nozero.shared.data.mapper.toDomain
import com.nozero.shared.domain.model.Completion
import com.nozero.shared.domain.repository.CompletionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class CompletionRepositoryImpl(
    private val database: NoZeroDatabase
) : CompletionRepository {

    private val queries get() = database.noZeroDatabaseQueries

    override fun getCompletionsForHabit(habitId: String): Flow<List<Completion>> {
        return queries.getCompletionsForHabit(habitId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getCompletionsForDate(date: LocalDate): Flow<List<Completion>> {
        return queries.getCompletionsForDate(date.toString())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getCompletionRange(habitId: String, from: LocalDate, to: LocalDate): Flow<List<Completion>> {
        return queries.getCompletionRange(habitId, from.toString(), to.toString())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun recordCompletion(completion: Completion) {
        queries.insertCompletion(
            habitId = completion.habitId,
            date = completion.date.toString(),
            value_ = completion.value,
            status = completion.status.name
        )
    }

    override suspend fun deleteCompletion(habitId: String, date: LocalDate) {
        queries.deleteCompletion(habitId, date.toString())
    }
}
