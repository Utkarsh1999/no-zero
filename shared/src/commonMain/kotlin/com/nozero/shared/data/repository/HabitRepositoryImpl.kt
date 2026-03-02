package com.nozero.shared.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.nozero.shared.data.local.NoZeroDatabase
import com.nozero.shared.data.mapper.toDomain
import com.nozero.shared.data.mapper.toEntity
import com.nozero.shared.domain.model.Habit
import com.nozero.shared.domain.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl(
    private val database: NoZeroDatabase
) : HabitRepository {

    private val queries get() = database.noZeroDatabaseQueries

    override fun getActiveHabits(): Flow<List<Habit>> {
        return queries.getActiveHabits()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getHabitById(id: String): Flow<Habit?> {
        return queries.getHabitById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }
    }

    override suspend fun insertHabit(habit: Habit) {
        val data = habit.toEntity()
        queries.insertHabit(
            id = data.id,
            title = data.title,
            description = data.description,
            type = data.type,
            frequencyType = data.frequencyType,
            frequencyValue = data.frequencyValue,
            trackingType = data.trackingType,
            trackingTarget = data.trackingTarget,
            reinforcementStyle = data.reinforcementStyle,
            reminderTime = data.reminderTime,
            isArchived = data.isArchived,
            createdAt = data.createdAt,
            earnedGraceDays = data.earnedGraceDays
        )
    }

    override suspend fun updateHabit(habit: Habit) {
        val data = habit.toEntity()
        queries.updateHabit(
            title = data.title,
            description = data.description,
            frequencyType = data.frequencyType,
            frequencyValue = data.frequencyValue,
            trackingType = data.trackingType,
            trackingTarget = data.trackingTarget,
            reinforcementStyle = data.reinforcementStyle,
            reminderTime = data.reminderTime,
            earnedGraceDays = data.earnedGraceDays,
            id = data.id
        )
    }

    override suspend fun archiveHabit(id: String) {
        queries.archiveHabit(id)
    }

    override suspend fun deleteHabit(id: String) {
        queries.deleteHabit(id)
    }
}
