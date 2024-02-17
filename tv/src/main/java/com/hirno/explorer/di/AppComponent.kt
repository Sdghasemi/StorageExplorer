package com.hirno.explorer.di

/**
 * Koin dependency injection modules declaration
 */
val appComponent = listOf(
    coroutinesModule,
    dataModule,
    observerModule,
    viewModelModule,
)