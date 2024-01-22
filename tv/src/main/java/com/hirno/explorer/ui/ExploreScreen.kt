@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.hirno.explorer.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ButtonShape
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.hirno.explorer.R
import com.hirno.explorer.model.ExplorerComposeState
import com.hirno.explorer.model.ExplorerScreenState
import com.hirno.explorer.model.Media
import com.hirno.explorer.ui.theme.AppTheme

@Composable
@ExperimentalMaterial3Api
@ExperimentalGlideComposeApi
fun ExploreScreen(
    modifier: Modifier = Modifier,
    model: ExplorerComposeState = ExplorerComposeState(),
) {
    Surface(
        modifier = modifier,
    ) {
        var focusedMedia by rememberSaveable {
            mutableStateOf<Media?>(null)
        }
        AnimatedContent(
            targetState = focusedMedia,
            transitionSpec = {
                fadeIn(animationSpec = tween(durationMillis = 500)) togetherWith
                        fadeOut(animationSpec = tween(durationMillis = 500))
            },
            label = "",
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
        ) { item ->
            GlideImage(model = item?.slides?.first(), contentDescription = "Preview Image")
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchField(
                modifier = Modifier,
                onSearch = model.onSearch,
            )
            val state by model.state
            when (val currentState = state) {
                is ExplorerScreenState.SearchResults -> {
                    ExploreResults(
                        onMediaClicked = model.onMediaClicked,
                        onMediaLongClicked = model.onMediaLongClicked,
                        onMediaFocused = { focusedMedia = it },
                        list = currentState.media,
                    )
                }
            }
        }
    }
}

@ExperimentalTvMaterial3Api
@Composable
@ExperimentalMaterial3Api
private fun SearchField(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    var searchedTerm by rememberSaveable { mutableStateOf("") }
    var isFocused by rememberSaveable { mutableStateOf(false) }
//    val animationInterpolator = remember {
//        DecelerateInterpolator()
//    }
    val width by animateFloatAsState(
        targetValue = if (isFocused) 1f else .95f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "Width Animation"
    )
    val elevation by animateDpAsState(
        targetValue = if (isFocused) 6.dp else 2.dp,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        label = "Elevation Animation"
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 12.dp, bottom = 9.dp, start = 12.dp, end = 12.dp),
    ) {
        TextField(
            value = searchedTerm,
            onValueChange = { newTerm ->
                searchedTerm = newTerm
                val term = newTerm.trim()
                if (term.length >= 3) {
                    onSearch(term)
                }
            },
            colors = TextFieldDefaults.colors(
//                backgroundColor = Color(0XFF101921),
//                placeholderColor = Color(0XFF888D91),
//                leadingIconColor = Color(0XFF888D91),
//                trailingIconColor = Color(0XFF888D91),
//                textColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
//                cursorColor = Color(0XFF070E14),
            ),
            singleLine = true,
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "") },
            placeholder = { Text(text = "Search...") },
            shape = RoundedCornerShape(12.dp),
            modifier = modifier
                .fillMaxWidth(width)
                .shadow(elevation = elevation, shape = RoundedCornerShape(12.dp))
                .align(Alignment.Center)
                .onFocusChanged { isFocused = it.isFocused },
        )
    }
//    BasicTextField(
//        value = searchedTerm,
//        onValueChange = { newTerm ->
//            searchedTerm = newTerm
//            val term = newTerm.trim()
//            if (term.length >= 3) {
//                onSearch(term)
//            }
//        },
//        singleLine = true,
//        modifier = modifier
//            .fillMaxWidth(width)
//            .padding(all = 12.dp)
//            .onFocusChanged { isFocused = it.isFocused },
//    )
}

@Composable
fun ExploreResults(
    onMediaClicked: (Media) -> Unit = {},
    onMediaLongClicked: (Media) -> Unit = {},
    onMediaFocused: (Media) -> Unit = {},
    list: List<Media>,
) {
    TvLazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(list) { item ->
            ExploreResult(
                onClick = onMediaClicked,
                onLongClick = onMediaLongClicked,
                onItemFocused = onMediaFocused,
                item = item,
            )
        }
    }
}

@Composable
fun ExploreResult(
    onClick: (Media) -> Unit = {},
    onLongClick: (Media) -> Unit = {},
    onItemFocused: (Media) -> Unit = {},
    item: Media = Media(),
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    Button(
        onClick = { onClick(item) },
        onLongClick = { onLongClick(item) },
        shape = ButtonDefaults.shape(shape = RoundedCornerShape(12.dp)),
        scale = ButtonDefaults.scale(
            scale = .95f,
            focusedScale = 1f,
        ),
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                if (isFocused) onItemFocused(item)
            },
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (item.isVideo) {
                Image(
                    painter = painterResource(id = R.drawable.video),
                    contentDescription = "Video Icon",
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.image),
                    contentDescription = "Image Icon",
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                ) {
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = item.file.nameWithoutExtension,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.wrapContentWidth(),
                        text = item.file.extension,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 2.dp),
                ) {
                    Text(
                        text = item.storageDescription,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = item.trimmedPath,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    item.duration?.let { duration ->
                        Text(text = duration.toString() + " º " + item.height)
                    }
                    Text(text = item.size.toString())
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
@ExperimentalMaterial3Api
fun SearchBarPreview() {
    AppTheme {
        SearchField()
    }
}

@Preview(showBackground = true)
@Composable
fun ExploreResultPreview() {
    AppTheme {
        ExploreResult(item = Media(
            path = "/storage/Drive/Path/To/File/FileName.mp4",
            storageUuid = "Drive",
            storageDescription = "L",
            mimeType = "video/mp4",
            duration = 126_000,
            width = 3840,
            height = 2160,
        ))
    }
}

@Preview
@Composable
@ExperimentalMaterial3Api
@ExperimentalGlideComposeApi
fun ExploreScreenPreview() {
    AppTheme {
        val state = remember {
            mutableStateOf(
                ExplorerScreenState.SearchResults(
                    media = listOf(
                        Media(
                            path = "/storage/Drive/Path/To/File/FileName.mp4",
                            storageUuid = "Drive",
                            storageDescription = "L",
                            mimeType = "video/mp4",
                            duration = 126_000,
                            width = 3840,
                            height = 2160,
                        )
                    )
                )
            )
        }
        ExploreScreen(
            modifier = Modifier.fillMaxSize(),
            model = ExplorerComposeState(
                state = state
            )
        )
    }
}