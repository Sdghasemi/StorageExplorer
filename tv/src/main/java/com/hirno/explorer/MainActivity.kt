package com.hirno.explorer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.hirno.explorer.model.ExplorerComposeState
import com.hirno.explorer.model.ExplorerScreenEffect
import com.hirno.explorer.model.ExplorerScreenEffect.OpenMedia
import com.hirno.explorer.model.ExplorerScreenEvent
import com.hirno.explorer.model.ExplorerScreenState
import com.hirno.explorer.model.Media
import com.hirno.explorer.ui.ExploreScreen
import com.hirno.explorer.ui.theme.AppTheme
import com.hirno.explorer.util.alsoIf
import com.hirno.explorer.viewmodel.ExplorerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Loads [MainFragment].
 */
class MainActivity : ComponentActivity() {
    
    private val model: ExplorerViewModel by viewModel()

    @ExperimentalGlideComposeApi
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(darkTheme = true) {
                ExploreScreen(
                    modifier = Modifier.fillMaxSize(),
                    model = ExplorerComposeState(
                        state = model.obtainState.observeAsState(ExplorerScreenState.SearchResults()),
                        onSearch = { model.event(ExplorerScreenEvent.Search(it)) },
                        onMediaClicked = {model.event(ExplorerScreenEvent.SimpleMediaSelect(it)) },
                        onMediaLongClicked = { model.event(ExplorerScreenEvent.ChooserMediaSelect(it)) },
                    ),
                )
            }
        }

        observeEffects()
    }

    private fun observeEffects() {
        model.obtainEffect.observe(this) { effect ->
            when (effect) {
                is OpenMedia -> openMedia(effect.media, effect is ExplorerScreenEffect.ChooserOpenMedia)
            }
        }
    }

    private fun openMedia(media: Media, forceChooser: Boolean) {
        try {
            val mediaUri = FileProvider.getUriForFile(this, packageName, media.file)
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(mediaUri, media.mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }.alsoIf(forceChooser) { intent ->
                Intent.createChooser(intent, getString(R.string.choose))
            })
        } catch (ignore: Exception) {}
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        model.event(ExplorerScreenEvent.ScreenLoad)
    }
}