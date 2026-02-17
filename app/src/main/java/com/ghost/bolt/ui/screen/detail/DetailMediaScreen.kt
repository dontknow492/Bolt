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
    mediaId: Int,
    title: String?,
    coverPath: String?,
    backdropPath: String?,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    onGenreClick: (Int, String) -> Unit
) {
    LaunchedEffect(mediaId) {
        Timber.tag("Detail Screen").v("Loading media id: $mediaId")
        viewModel.loadDetailData(mediaId, coverPath, title, backdropPath)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    DynamicThemeFromImage(
        imageUrl = coverPath
    ) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            DetailedMediaContent(
                uiState = uiState,
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refreshData,
                onRetry = viewModel::loadDetailData,
                mediaId = mediaId,
                title = title,
                coverPath = coverPath,
                backdropPath = backdropPath,
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
    isRefreshing: Boolean,
    onRefresh: (Int) -> Unit,
    onRetry: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    mediaId: Int,
    title: String?,
    coverPath: String?,
    backdropPath: String?,
    onBackClick: () -> Unit,
    onCastClick: (Int) -> Unit,
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    onGenreClick: (Int, String) -> Unit
) {

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh(mediaId) },
        modifier = modifier
    ) {
        when (val state = uiState) {
            is DetailUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = {
                        onRetry(
                            mediaId,
                            coverPath,
                            title,
                            backdropPath
                        )
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



