package com.ghost.bolt.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.ui.viewModel.HomeUiState
import com.ghost.bolt.ui.viewModel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMediaClick: (Int) -> Unit,
    mediaType: AppMediaType
) {
    val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = homeUiState) {
        is HomeUiState.Error -> {
            Text(state.message)
        }

        HomeUiState.Loading -> {
            Text(text = "Loading")
        }

        is HomeUiState.Success -> {
            val topMedia = state.data.popularMovies.collectAsLazyPagingItems()
            LazyColumn {
                items(
                    topMedia.itemCount,
                    key = { index -> topMedia[index]?.id ?: index }) { index ->
                    topMedia.get(index)?.let { media ->
                        MediaItem(data = media)
                    }
                }
            }
        }
    }

    LaunchedEffect(mediaType) {
        viewModel.load(mediaType)
    }
}

@Composable
fun MediaItem(modifier: Modifier = Modifier, data: MediaEntity) {
    Column {
        Text(data.title)
    }
}
