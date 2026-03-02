package com.nozero.shared.domain.usecase

import com.nozero.shared.domain.model.Habit
import com.nozero.shared.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow

/**
 * Returns all currently active habits for the Today screen.
 */
class GetActiveHabitsUseCase(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> {
        return habitRepository.getActiveHabits()
    }
}
