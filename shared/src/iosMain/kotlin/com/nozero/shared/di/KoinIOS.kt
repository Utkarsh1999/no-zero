package com.nozero.shared.di

import com.nozero.shared.data.local.AppPreferences
import com.nozero.shared.data.local.DatabaseDriverFactory
import com.nozero.shared.notification.NotificationScheduler
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initKoin() {
    startKoin {
        modules(
            sharedModule,
            module {
                single { DatabaseDriverFactory() }
                single { AppPreferences() }
                single { NotificationScheduler() }
            }
        )
    }
}
