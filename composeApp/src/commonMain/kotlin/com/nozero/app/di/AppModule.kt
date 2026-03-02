package com.nozero.app.di

import com.nozero.app.viewmodel.HabitDetailViewModel
import com.nozero.app.viewmodel.OnboardingViewModel
import com.nozero.app.viewmodel.TodayViewModel
import org.koin.dsl.module

val appModule = module {
    factory { TodayViewModel(get(), get(), get(), get(), get(), get()) }
    factory { OnboardingViewModel(get(), get()) }
    factory { HabitDetailViewModel(get(), get(), get(), get(), get()) }
}
