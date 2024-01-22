package com.hirno.explorer.di

import androidx.room.Room
import com.hirno.explorer.data.source.DefaultMediaRepository
import com.hirno.explorer.data.source.MediaDataSource
import com.hirno.explorer.data.source.MediaRepository
import com.hirno.explorer.data.source.local.db.RecentDatabase
import com.hirno.explorer.data.source.local.db.MediaDatabaseDataSource
import com.hirno.explorer.data.source.local.db.MediaDao
import com.hirno.explorer.data.source.local.storage.MediaStorageDataSource
import com.hirno.explorer.viewmodel.ExplorerViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin dependency injection modules declaration
 */
val appModule = module {
    single(named("IODispatcher")) {
        Dispatchers.IO
    }
    single {
        Room.databaseBuilder(
            get(),
            RecentDatabase::class.java,
            "RecentDB"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single<MediaDao> {
        val database = get<RecentDatabase>()
        database.mediaDao()
    }
    single<MediaDataSource>(named("StorageDataSource")) { MediaStorageDataSource(get(named("IODispatcher")), get()) }
    single<MediaDataSource>(named("DatabaseDataSource")) { MediaDatabaseDataSource(get(named("IODispatcher")), get()) }
    single<MediaRepository> { DefaultMediaRepository(get(named("StorageDataSource")), get(named("DatabaseDataSource"))) }
    viewModel { ExplorerViewModel(get(), get()) }
}