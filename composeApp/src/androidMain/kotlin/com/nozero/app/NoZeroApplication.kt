package com.nozero.app

import android.app.Application
import com.nozero.app.di.appModule
import com.nozero.shared.data.local.AppPreferences
import com.nozero.shared.data.local.DatabaseDriverFactory
import com.nozero.shared.di.sharedModule
import com.nozero.shared.notification.NotificationScheduler
import org.koin.core.context.startKoin
import org.koin.dsl.module

class NoZeroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                module {
                    single { DatabaseDriverFactory(this@NoZeroApplication) }
                    single { AppPreferences(this@NoZeroApplication) }
                    single { NotificationScheduler(this@NoZeroApplication) }
                },
                sharedModule,
                appModule
            )
        }
    }
}
