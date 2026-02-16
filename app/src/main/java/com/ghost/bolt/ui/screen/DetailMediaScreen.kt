package com.ghost.bolt.ui.screen

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghost.bolt.database.entity.TmdbImageSize
import com.ghost.bolt.database.entity.getPosterUrl
import com.ghost.bolt.ui.components.DynamicThemeFromImage
import com.ghost.bolt.ui.viewModel.DetailUiState
import com.ghost.bolt.ui.viewModel.DetailViewModel
import timber.log.Timber

@Composable
fun DetailedMediaScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    mediaId: Int,
) {
    LaunchedEffect(mediaId) {
        Timber.tag("Detail Screen").v("Loading media id: $mediaId")
        viewModel.loadDetailData(mediaId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is DetailUiState.Error -> {
            Text(state.message)
        }

        DetailUiState.Loading -> {
            Text(text = "Loading")
        }

        is DetailUiState.Success -> {
            DynamicThemeFromImage(
                imageUrl = state.data.media.getPosterUrl(TmdbImageSize.W92),
            ) {
                Button({}) {
                    Text("Success: ${MaterialTheme.colorScheme.primary}")
                }
            }

        }

    }
}