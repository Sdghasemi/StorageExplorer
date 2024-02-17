package com.hirno.explorer

import android.app.Application
import com.hirno.explorer.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
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
            module {
                single { this@ExplorerApplication }
            }
            modules(appComponent)
        }
    }
}