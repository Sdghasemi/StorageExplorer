package com.hirno.explorer.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coroutinesModule = module {
    single(named("IODispatcher")) {
        Dispatchers.IO
    }
    single<CoroutineScope> { CoroutineScope(SupervisorJob()) }
}