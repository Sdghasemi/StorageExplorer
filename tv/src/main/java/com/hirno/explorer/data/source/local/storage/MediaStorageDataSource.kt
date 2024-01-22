package com.hirno.explorer.data.source.local.storage

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log
import android.widget.Toast
import androidx.core.text.htmlEncode
import com.hirno.explorer.data.source.MediaDataSource
import com.hirno.explorer.model.Media
import com.hirno.explorer.util.getMimeType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Media local data source implementation
 *
 * @property ioDispatcher The coroutines dispatcher used to perform file IO operations on
 */
class MediaStorageDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val application: Application,
) : MediaDataSource {
    companion object {
        const val TAG = "MediaStorageDataSource"
        private val ROOT_SEARCH_DIRS = listOf(
            "Films",
            "Series",
        )
    }

    /**
     * Get all media from local storage
     */
    override suspend fun searchMedia(term: String): Flow<List<Media>> = flow {
        val results = mutableListOf<Media>()
        getUsbStoragePaths().forEach { (storage, description) ->
            Log.d(TAG, "Storage path found: ${storage.path}")
            ROOT_SEARCH_DIRS.forEach { targetDir ->
                File(storage, targetDir).let { target ->
                    searchMediaIn(target, storage.name, description, term, results)
                }
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun saveRecentMedia(media: Media) = false

    private suspend fun FlowCollector<List<Media>>.searchMediaIn(
        target: File,
        storageUuid: String,
        description: String,
        term: String,
        results: MutableList<Media>,
    ) {
        if (target.isFile) {
            if (validateAndAddMedia(target, storageUuid, description, results)) {
                Log.d(TAG, "File found for \"$term\": ${results.last()}")
                emit(results)
            }
        } else {
            target.listFiles { dir, name ->
                name.contains(term, ignoreCase = true) ||
                        dir.name.contains(term, ignoreCase = true)
            }?.forEach { innerDirectory ->
                searchMediaIn(
                    target = innerDirectory,
                    storageUuid = storageUuid,
                    description = description,
                    term = term,
                    results = results
                )
            } /*?: Log.d(TAG, "null listFiles() for $target")*/
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun getUsbStoragePaths(): List<Pair<File, String>> {
        val storageManger = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageDir = File("/storage/")
        return storageManger.storageVolumes.mapNotNull { storage ->
            storage.uuid?.let {
                File(storageDir, it) to storage.getDescription(application)
            }
        }
    }

    private fun validateAndAddMedia(
        file: File,
        storageUuid: String,
        description: String,
        results: MutableList<Media>,
    ): Boolean {
        try {
            val mimeType = getMimeType(file) ?: ""
            if (mimeType.isValidMedia) {
                var duration: Long? = null
                var width: Int? = null
                var height: Int? = null
                var slides = mutableListOf<String>()
                if (mimeType.startsWith("video")) {
                    MediaMetadataRetriever().use { retriever ->
                        retriever.setDataSource(file.path)
                        duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                        width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()
                        height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()
                        retriever.primaryImage?.saveToFile(file.path.htmlEncode())?.path?.let { slides.add(it) }
                    }
                }

                val media = Media(
                    path = file.path,
                    storageUuid = storageUuid,
                    storageDescription = description,
                    mimeType = mimeType,
                    duration = duration,
                    width = width,
                    height = height,
                    slides = slides,
                )
                return results.add(media)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not read file \"$file\": ${e.message}")
        }
        return false
    }

    private fun Bitmap.saveToFile(name: String): File? {
        val dir = File(application.cacheDir, name)
        if (!dir.exists()) {
            if (!dir.mkdirs()) Log.i(TAG, "Can't create directory to save the image")
        }
        val slideFile = File(dir, "${System.currentTimeMillis()}.png")
        try {
            slideFile.createNewFile()
            FileOutputStream(slideFile).use { stream ->
                compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
            }
            return slideFile
        } catch (e: IOException) {
            Log.i(TAG, "Issue while saving image: $slideFile", e)
        }
        return null
    }

    private val File.isReadableDirectory: Boolean
        get() = exists() && !isFile && canRead()
    private val String.isValidMedia: Boolean
        get() = contains("image") || contains("video")
}