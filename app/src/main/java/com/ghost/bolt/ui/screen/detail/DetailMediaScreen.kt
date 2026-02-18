package com.ghost.bolt.ui.screen.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.ui.components.DynamicThemeFromImage
import com.ghost.bolt.ui.components.ErrorScreen
import com.ghost.bolt.ui.viewModel.DetailUiState
import com.ghost.bolt.ui.viewModel.DetailViewModel

import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedMediaScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    media: MediaCardUiModel,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onMediaClick: (media: MediaCardUiModel) -> Unit,
    onGenreClick: (Int, String) -> Unit
) {
    LaunchedEffect(media) {
        Timber.tag("Detail Screen").v("Loading media for: $media")
        viewModel.loadDetailData(media)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    DynamicThemeFromImage(
        imageUrl = media.posterUrl
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            DetailedMediaContent(
                uiState = uiState,
                media = media,
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refreshData,
                onRetry = viewModel::loadDetailData,
                onBackClick = onBackClick,
                onCastClick = onCastClick,
                onMediaClick = onMediaClick,
                onGenreClick = onGenreClick
            )
        }
    }
}

@Composable
private fun DetailedMediaContent(
    modifier: Modifier = Modifier,
    uiState: DetailUiState,
    media: MediaCardUiModel,
    isRefreshing: Boolean,
    onRefresh: (media: MediaCardUiModel) -> Unit,
    onRetry: (MediaCardUiModel) -> Unit,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onMediaClick: (media: MediaCardUiModel) -> Unit,
    onGenreClick: (Int, String) -> Unit
) {

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh(media) },
        modifier = modifier
    ) {
        when (val state = uiState) {
            is DetailUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = {
                        onRetry(media)
                    })
            }

            is DetailUiState.Success -> {
                DetailSuccessContent(
                    detail = state.data,
                    onCastClick = onCastClick,
                    onMediaClick = onMediaClick,
                    onGenreClick = onGenreClick,
                )
            }

            is DetailUiState.Loading -> {
                DetailLoadingScreen(
                    modifier = Modifier.fillMaxSize(),
                    state.backdropPath
                )


            }
        }
    }
}



