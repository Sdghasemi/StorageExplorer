package com.hirno.explorer.di

import com.hirno.explorer.storage.DefaultStorageObserver
import com.hirno.explorer.storage.StorageObserver
import org.koin.core.qualifier.named
import org.koin.dsl.module

val observerModule = module {
    single<StorageObserver> { DefaultStorageObserver(get(), get(), get(named("IODispatcher"))) }
}