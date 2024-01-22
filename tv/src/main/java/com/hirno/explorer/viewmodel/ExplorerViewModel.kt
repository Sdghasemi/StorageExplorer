package com.hirno.explorer.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hirno.explorer.data.source.MediaRepository
import com.hirno.explorer.model.ExplorerComposeState
import com.hirno.explorer.util.liveData
import com.hirno.explorer.model.ExplorerScreenEffect
import com.hirno.explorer.model.ExplorerScreenEvent
import com.hirno.explorer.model.ExplorerScreenEvent.MediaSelect
import com.hirno.explorer.model.ExplorerScreenEvent.ScreenLoad
import com.hirno.explorer.model.ExplorerScreenEvent.Search
import com.hirno.explorer.model.ExplorerScreenState
import kotlinx.coroutines.launch

class ExplorerViewModel(
    savedState: SavedStateHandle,
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val viewState: MutableLiveData<ExplorerScreenState> by savedState.liveData()

    private val viewAction: MutableLiveData<ExplorerScreenEffect> by savedState.liveData()

    val obtainState: LiveData<ExplorerScreenState> = viewState

    val obtainEffect: LiveData<ExplorerScreenEffect> = viewAction

    fun event(event: ExplorerScreenEvent) {
        when(event) {
            is ScreenLoad -> viewModelScope.launch {
                mediaRepository.getMedia().collect { mediaList ->
                    viewState.value = ExplorerScreenState.SearchResults(
                        term = "",
                        media = mediaList,
                    )
                }
            }
            is Search -> viewModelScope.launch {
                val searchTerm = event.term
                mediaRepository.getMedia(searchTerm).collect { mediaList ->
                    viewState.value = ExplorerScreenState.SearchResults(
                        term = searchTerm,
                        media = mediaList,
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
}