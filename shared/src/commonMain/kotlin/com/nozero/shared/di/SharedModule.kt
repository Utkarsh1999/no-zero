package com.nozero.shared.di

import com.nozero.shared.data.local.DatabaseDriverFactory
import com.nozero.shared.data.local.NoZeroDatabase
import com.nozero.shared.data.repository.CompletionRepositoryImpl
import com.nozero.shared.data.repository.HabitRepositoryImpl
import com.nozero.shared.domain.repository.CompletionRepository
import com.nozero.shared.domain.repository.HabitRepository
import com.nozero.shared.domain.usecase.CalculateConsistencyUseCase
import com.nozero.shared.domain.usecase.CreateHabitUseCase
import com.nozero.shared.domain.usecase.GetActiveHabitsUseCase
import com.nozero.shared.domain.usecase.ProcessGraceDaysUseCase
import com.nozero.shared.domain.usecase.ToggleCompletionUseCase
import com.nozero.shared.motivation.MotivationEngine
import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule: Module = module {
    // Database
    single { get<DatabaseDriverFactory>().createDriver() }
    single { NoZeroDatabase(get()) }

    // Repositories
    single<HabitRepository> { HabitRepositoryImpl(get()) }
    single<CompletionRepository> { CompletionRepositoryImpl(get()) }

    // Use Cases
    factory { GetActiveHabitsUseCase(get()) }
    factory { ToggleCompletionUseCase(get(), get(), get()) }
    factory { ProcessGraceDaysUseCase(get(), get()) }
    factory { CreateHabitUseCase(get(), get()) }
    factory { CalculateConsistencyUseCase() }

    // Motivation
    single { MotivationEngine() }
}
