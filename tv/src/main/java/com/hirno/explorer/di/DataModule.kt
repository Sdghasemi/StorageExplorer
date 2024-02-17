package com.hirno.explorer.di

import androidx.room.Room
import com.hirno.explorer.data.source.DefaultMediaRepository
import com.hirno.explorer.data.source.MediaDataSource
import com.hirno.explorer.data.source.MediaRepository
import com.hirno.explorer.data.source.local.db.MediaDao
import com.hirno.explorer.data.source.local.db.MediaDatabaseDataSource
import com.hirno.explorer.data.source.local.db.RecentDatabase
import com.hirno.explorer.data.source.local.db.SlideDao
import com.hirno.explorer.data.source.local.storage.MediaStorageDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(
            get(),
            RecentDatabase::class.java,
            "RecentDB"
        ).fallbackToDestructiveMigration()
            .build()
    }
    single<MediaDao> { get<RecentDatabase>().mediaDao() }
    single<SlideDao> { get<RecentDatabase>().slideDao() }
    single<MediaDataSource>(named("StorageDataSource")) { MediaStorageDataSource(get(named("IODispatcher")), get(), get()) }
    single<MediaDataSource>(named("DatabaseDataSource")) { MediaDatabaseDataSource(get(named("IODispatcher")), get(), get()) }
    single<MediaRepository> { DefaultMediaRepository(get(named("StorageDataSource")), get(named("DatabaseDataSource"))) }
}