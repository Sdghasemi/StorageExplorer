package com.hirno.explorer.data.source.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hirno.explorer.model.Media

/**
 * The Room Database that contains the Collections table.
 */
@Database(entities = [Media::class, Media.Slide::class], version = 5, exportSchema = true)
abstract class RecentDatabase : RoomDatabase() {
    abstract fun mediaDao(): MediaDao
    abstract fun slideDao(): SlideDao
}