package com.hirno.explorer.data.source

import android.util.Log
import com.hirno.explorer.model.Media

const val TAG = "DefaultMediaRepository"

/**
 * Concrete implementation to load media files from the data source and report completion
 */
class DefaultMediaRepository(
    private val mediaStorageDataSource: MediaDataSource,
    private val mediaDatabaseDataSource: MediaDataSource,
) : MediaRepository {
    override suspend fun getMedia(term: String) = when {
        term.isBlank() -> mediaDatabaseDataSource.searchMedia()
        else -> mediaStorageDataSource.searchMedia(term)
    }

    override suspend fun saveRecentMedia(media: Media): Boolean {
        return mediaDatabaseDataSource.saveRecentMedia(media)
    }
}