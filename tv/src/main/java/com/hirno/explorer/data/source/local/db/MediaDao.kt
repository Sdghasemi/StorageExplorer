package com.hirno.explorer.data.source.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hirno.explorer.model.Media

/**
 * Data Access Object for Collections table
 */
@Dao
interface MediaDao {
    /**
     * Select all collections from the Collections table.
     *
     * @return all loaded collections.
     */
    @Query("SELECT * FROM Media ORDER BY lastUseMillis DESC")
    fun getAll(): List<Media>

    /**
     * Insert a recent media in the database.
     *
     * @param media the media to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(media: Media): Long

    /**
     * Delete all collections.
     */
    @Query("DELETE FROM Media")
    fun deleteAll()
}