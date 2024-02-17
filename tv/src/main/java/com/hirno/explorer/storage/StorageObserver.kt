package com.hirno.explorer.storage

import com.hirno.explorer.model.Storage
import kotlinx.coroutines.flow.StateFlow

interface StorageObserver {
    val connectedVolumes: StateFlow<List<Storage>>

    fun notifyVolumeChange()
}