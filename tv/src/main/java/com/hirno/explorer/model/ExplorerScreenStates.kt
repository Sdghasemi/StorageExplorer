package com.hirno.explorer.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.Parcelize

@Stable
@Immutable
data class ExplorerComposeState(
    val state: State<ExplorerScreenState> = mutableStateOf(ExplorerScreenState.SearchResults()),
    val onSearch: (String) -> Unit = {},
    val onMediaClicked: (Media) -> Unit = {},
    val onMediaLongClicked: (Media) -> Unit = {},
)

sealed class ExplorerScreenState {
    @Parcelize
    data class SearchResults(
        val term: String = "",
        val media: List<Media> = listOf(),
        private var resultsHash: Int = -1,
    ) : ExplorerScreenState(), Parcelable {
        init {
            resultsHash = media.hashCode()
        }
    }
}

sealed class ExplorerScreenEvent {
    data object ScreenLoad : ExplorerScreenEvent()
    data class Search(
        val term: String = "",
    ) : ExplorerScreenEvent()
    abstract class MediaSelect(
        open val media: Media,
    ) : ExplorerScreenEvent()
    data class SimpleMediaSelect(
        override val media: Media = Media(),
    ) : MediaSelect(media)
    data class ChooserMediaSelect(
        override val media: Media = Media(),
    ) : MediaSelect(media)
}

sealed class ExplorerScreenEffect {
    abstract class OpenMedia(
        open val media: Media,
    ) : ExplorerScreenEffect()
    data class SimpleOpenMedia(
        override val media: Media = Media(),
    ) : OpenMedia(media)
    class ChooserOpenMedia(
        override val media: Media = Media(),
    ) : OpenMedia(media)
}