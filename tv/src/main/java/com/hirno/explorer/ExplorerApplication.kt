package com.hirno.explorer

import android.app.Application
import com.hirno.explorer.di.appModule
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ExplorerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupDependencyInjection()
    }

    private fun setupDependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(this@ExplorerApplication)
            modules(
                module {
                    single { this@ExplorerApplication }
                },
                appModule
            )
        }
    }
}