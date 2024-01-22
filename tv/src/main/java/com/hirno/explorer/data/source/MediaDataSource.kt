package com.hirno.explorer.data.source

import com.hirno.explorer.model.Media
import kotlinx.coroutines.flow.Flow

/**
 * Main entry point for accessing media files.
 */
interface MediaDataSource {
    suspend fun searchMedia(term: String = ""): Flow<List<Media>>

    suspend fun saveRecentMedia(media: Media): Boolean
}