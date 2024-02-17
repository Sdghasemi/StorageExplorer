package com.hirno.explorer.storage

import android.app.Application
import android.content.Context
import android.os.storage.StorageManager
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import com.hirno.explorer.model.Storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.seconds


class DefaultStorageObserver(
    private val application: Application,
    private val coroutineScope: CoroutineScope,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : StorageObserver, DefaultLifecycleObserver {

    private val storageManager = application.getSystemService(Context.STORAGE_SERVICE) as StorageManager

    override val connectedVolumes: MutableStateFlow<List<Storage>> = MutableStateFlow(storages)

    private var volumeChangeChecker: Job? = null

    override fun notifyVolumeChange() {
        Log.d(TAG, "Volume changes detected!")
        volumeChangeChecker?.cancel()
        volumeChangeChecker = coroutineScope.launch(ioDispatcher) {
            for (time in 0 .. STORAGE_CHECK_TIME_OUT_MILLIS step STORAGE_CHECK_INTERVAL_MILLIS) {
                connectedVolumes.emit(storages)
                delay(STORAGE_CHECK_INTERVAL_MILLIS)
            }
        }
    }

    private val storages: List<Storage>
        get() =  try {
            Log.i(TAG, "Retrieving storage volumes")
            storageManager.storageVolumes.mapNotNull { volume ->
                volume.uuid?.let { uuid ->
                    Storage(
                        uuid = uuid,
                        path = File(STORAGE_DIR, uuid),
                        description = volume.getDescription(application),
                    ).also { storage ->
                        Log.d(TAG, "Storage found: $storage")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error while retrieving storage paths", e)
            emptyList()
        }

    companion object {
        const val TAG = "DefaultStorageObserver"
        const val STORAGE_PATH = "storage/"
        val STORAGE_DIR = File(STORAGE_PATH)
        private val STORAGE_CHECK_INTERVAL_MILLIS = 1.seconds.inWholeMilliseconds
        private val STORAGE_CHECK_TIME_OUT_MILLIS = 30.seconds.inWholeMilliseconds
    }
}