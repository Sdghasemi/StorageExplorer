package com.hirno.explorer.di

import com.hirno.explorer.viewmodel.ExplorerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ExplorerViewModel(get(), get(), get(), get()) }
}