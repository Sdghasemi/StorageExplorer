package com.hirno.explorer.data.source.local.storage

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log
import com.hirno.explorer.data.source.MediaDataSource
import com.hirno.explorer.model.Media
import com.hirno.explorer.util.getMimeType
import com.hirno.explorer.util.substringAfter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


/**
 * Media local data source implementation
 *
 * @property ioDispatcher The coroutines dispatcher used to perform file IO operations on
 */
class MediaStorageDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val application: Application,
) : MediaDataSource {
    /**
     * Get all media from local storage
     */
    override suspend fun searchMedia(term: String): Flow<List<Media>> = flow {
        try {
            val results = mutableListOf<Media>()
            getUsbStoragePaths().forEach { (storage, description) ->
                Log.d(TAG, "Storage path found: ${storage.path}")
                ROOT_SEARCH_DIRS.forEach { targetDir ->
                    val target = File(storage, targetDir)
                    searchMediaIn(target, storage.name, description, term, results)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in accessing storages", e)
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
        try {
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
                    Log.d(TAG, "Looking in $innerDirectory")
                    searchMediaIn(
                        target = innerDirectory,
                        storageUuid = storageUuid,
                        description = description,
                        term = term,
                        results = results
                    )
                } ?: run {
                    if (target.canRead()) Log.d(TAG, "No folders under $target")
                    else Log.e(TAG, "Cannot access $target")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while searching media", e)
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun getUsbStoragePaths(): List<Pair<File, String>> {
        return try {
            val storageManger = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val storageDir = File(STORAGE_PATH)
            storageManger.storageVolumes.mapNotNull { storage ->
                storage.uuid?.let {
                    File(storageDir, it) to storage.getDescription(application)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while retrieving storage paths", e)
            emptyList()
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
                val slides = mutableListOf<Media.Slide>()
                if (mimeType.startsWith("video")) {
                    MediaMetadataRetriever().use { retriever ->
                        retriever.setDataSource(file.path)
                        duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
                        width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt()
                        height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt()
                        val slideName = file.slidePath
                        val durationInSec = duration?.milliseconds?.inWholeSeconds ?: 30.minutes.inWholeSeconds
                        val slideDistance: Long = 10.minutes.inWholeSeconds
                        (1..4).mapNotNull { slideNum ->
                            (slideNum * slideDistance).takeUnless { it >= durationInSec }
                        }.forEach { frameInSec ->
                            try {
                                retriever.getFrameAtTime(frameInSec.seconds.inWholeMicroseconds, OPTION_CLOSEST)?.let { slide ->
                                    slide.saveToFile(slideName)?.path?.let { slidePath ->
                                        slides.add(Media.Slide(file.path, slidePath))
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w(TAG, "Failed to create slide on ${frameInSec}s for \"$file\": ${e.message}")
                            }
                        }

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
                    slides = slides.toList().takeUnless { it.isEmpty() },
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

    private val File.slidePath: String
        get() = path.substringAfter(STORAGE_PATH)
    private val File.isReadableDirectory: Boolean
        get() = exists() && !isFile && canRead()
    private val String.isValidMedia: Boolean
        get() = contains("image") || contains("video")

    companion object {
        const val TAG = "MediaStorageDataSource"
        const val STORAGE_PATH = "storage/"
        private val ROOT_SEARCH_DIRS = listOf(
            "Films",
            "Series",
        )
    }
}