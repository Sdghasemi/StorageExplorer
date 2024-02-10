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
interface SlideDao {
    /**
     * Insert recent slides in the database.
     *
     * @param slides the slides to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(slides: List<Media.Slide>): List<Long>

    /**
     * Delete all collections.
     */
    @Query("DELETE FROM Slides")
    fun deleteAll()
}