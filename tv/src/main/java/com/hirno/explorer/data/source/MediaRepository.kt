package com.hirno.explorer.data.source

import com.hirno.explorer.model.Media
import kotlinx.coroutines.flow.Flow

/**
 * Interface to the data layer.
 */
interface MediaRepository {
    suspend fun getMedia(term: String = ""): Flow<List<Media>>

    suspend fun saveRecentMedia(media: Media): Boolean
}