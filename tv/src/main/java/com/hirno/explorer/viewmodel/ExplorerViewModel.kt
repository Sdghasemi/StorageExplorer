package com.hirno.explorer.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hirno.explorer.data.source.MediaRepository
import com.hirno.explorer.util.liveData
import com.hirno.explorer.model.ExplorerScreenEffect
import com.hirno.explorer.model.ExplorerScreenEvent
import com.hirno.explorer.model.ExplorerScreenEvent.MediaSelect
import com.hirno.explorer.model.ExplorerScreenEvent.RequestStoragePermission
import com.hirno.explorer.model.ExplorerScreenEvent.ScreenLoad
import com.hirno.explorer.model.ExplorerScreenEvent.Search
import com.hirno.explorer.model.ExplorerScreenState
import com.hirno.explorer.storage.StorageObserver
import kotlinx.coroutines.launch

class ExplorerViewModel(
    private val application: Application,
    savedState: SavedStateHandle,
    private val mediaRepository: MediaRepository,
    storageObserver: StorageObserver,
) : AndroidViewModel(application) {

    private val viewState: MutableLiveData<ExplorerScreenState> by savedState.liveData()

    private val viewAction: MutableLiveData<ExplorerScreenEffect> by savedState.liveData()

    val obtainState: LiveData<ExplorerScreenState> = viewState

    val obtainEffect: LiveData<ExplorerScreenEffect> = viewAction

    private val connectedVolumes = storageObserver.connectedVolumes

    fun event(event: ExplorerScreenEvent) {
        when(event) {
            is RequestStoragePermission -> viewAction.value = ExplorerScreenEffect.RequestStoragePermission
            is ScreenLoad -> {
                val hasStoragePermission = ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
                if (hasStoragePermission) viewModelScope.launch {
                    connectedVolumes.collect { storages ->
                        Log.d(TAG, "Collected storages: $storages")
                        if (storages.isEmpty()) viewState.value = ExplorerScreenState.NoStorageFound
                        else mediaRepository.getMedia().collect { mediaList ->
                            viewState.value = ExplorerScreenState.SearchResults(
                                term = "",
                                media = mediaList,
                                connectedStorages = storages,
                            )
                        }
                    }
                } else viewState.value = ExplorerScreenState.StoragePermissionRequired
            }
            is Search -> viewModelScope.launch {
                val searchTerm = event.term
                mediaRepository.getMedia(searchTerm).collect { mediaList ->
                    viewState.value = ExplorerScreenState.SearchResults(
                        term = searchTerm,
                        media = mediaList,
                        connectedStorages = connectedVolumes.value,
                    )
                }
            }
            is MediaSelect -> {
                val selectedMedia = event.media
                viewAction.value = when (event) {
                    is ExplorerScreenEvent.ChooserMediaSelect -> ExplorerScreenEffect.ChooserOpenMedia(selectedMedia)
                    else -> ExplorerScreenEffect.SimpleOpenMedia(selectedMedia)
                }
                viewModelScope.launch {
                    mediaRepository.saveRecentMedia(selectedMedia)
                }
            }
        }
    }

    companion object {
        const val TAG = "ExploreViewModel"
    }
}