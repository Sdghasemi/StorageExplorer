package com.hirno.explorer.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parcelize

@Stable
@Immutable
data class ExplorerComposeState(
    val state: State<ExplorerScreenState> = mutableStateOf(ExplorerScreenState.SearchResults()),
    val onSearch: (String) -> Unit = {},
    val onMediaClicked: (Media) -> Unit = {},
    val onMediaLongClicked: (Media) -> Unit = {},
    val requestStoragePermission: () -> Unit = {},
)

sealed class ExplorerScreenState : Parcelable {
    @Parcelize
    data object StoragePermissionRequired : ExplorerScreenState()
    @Parcelize
    data object NoStorageFound : ExplorerScreenState()
    @Parcelize
    data class SearchResults(
        val term: String = "",
        val media: List<Media> = listOf(),
        val connectedStorages: List<Storage> = listOf(),
        private var resultsHash: Int = -1,
    ) : ExplorerScreenState() {
        init {
            resultsHash = media.size
        }
    }
}

sealed class ExplorerScreenEvent : Parcelable {
    @Parcelize
    data object ScreenLoad : ExplorerScreenEvent()
    @Parcelize
    data object RequestStoragePermission : ExplorerScreenEvent()
    @Parcelize
    data class Search(
        val term: String = "",
    ) : ExplorerScreenEvent()
    abstract class MediaSelect(
        open val media: Media,
    ) : ExplorerScreenEvent()
    @Parcelize
    data class SimpleMediaSelect(
        override val media: Media = Media(),
    ) : MediaSelect(media)
    @Parcelize
    data class ChooserMediaSelect(
        override val media: Media = Media(),
    ) : MediaSelect(media)
}

sealed class ExplorerScreenEffect : Parcelable {
    @Parcelize
    data object RequestStoragePermission : ExplorerScreenEffect()
    abstract class OpenMedia(
        open val media: Media,
    ) : ExplorerScreenEffect()
    @Parcelize
    data class SimpleOpenMedia(
        override val media: Media = Media(),
    ) : OpenMedia(media)
    @Parcelize
    class ChooserOpenMedia(
        override val media: Media = Media(),
    ) : OpenMedia(media)
}