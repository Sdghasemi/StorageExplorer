package com.hirno.explorer.data.source.local.db

import com.hirno.explorer.data.source.MediaDataSource
import com.hirno.explorer.model.Media
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Collections local data source implementation
 *
 * @property mediaDao The DAO reference used to perform database related actions
 * @property ioDispatcher The coroutines dispatcher used to perform DAO operations on
 */
class MediaDatabaseDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val mediaDao: MediaDao,
) : MediaDataSource {
    /**
     * Get all collections from local storage
     *
     * @return A [NetworkResponse.Success] when all collections are retrieved successfully or a [NetworkResponse.UnknownError] with the exception that occurred
     */
    override suspend fun searchMedia(term: String): Flow<List<Media>> = flow {
        emit(mediaDao.getAll())
    }.flowOn(ioDispatcher)

    override suspend fun saveRecentMedia(media: Media): Boolean = withContext(ioDispatcher) {
        try {
            media.updateLastUse()
            mediaDao.insert(media) != 0L
        } catch (e: Exception) {
            false
        }
    }

//    /**
//     * Deletes all existing collections from local storage and inserts the new ones
//     *
//     * @param collections The new collections
//     * @return `true` if the insertion was successful, `false` otherwise
//     */
//    override suspend fun cacheMedia(collections: CollectionResponseModel): Boolean {
//    }
//
//    /**
//     * Deletes all collections from local storage
//     *
//     */
//    override suspend fun deleteAllCollections() {
//        mediaDao.deleteAll()
//    }
}