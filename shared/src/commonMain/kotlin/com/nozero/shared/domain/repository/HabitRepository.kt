package com.nozero.shared.domain.repository

import com.nozero.shared.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getActiveHabits(): Flow<List<Habit>>
    fun getHabitById(id: String): Flow<Habit?>
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun archiveHabit(id: String)
    suspend fun deleteHabit(id: String)
}
