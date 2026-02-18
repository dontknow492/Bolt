package com.ghost.bolt.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.ui.components.ErrorScreen
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCardShimmer
import com.ghost.bolt.ui.components.card.MediaCardStyle
import com.ghost.bolt.ui.components.card.MediaEntityCard
import com.ghost.bolt.ui.viewModel.MediaGridUiData
import com.ghost.bolt.ui.viewModel.MediaGridUiState
import com.ghost.bolt.ui.viewModel.MediaGridViewModel

//@Composable
//fun MediaGrid(
//    modifier: Modifier = Modifier,

//    viewModel: MediaGridViewModel = hiltViewModel(),
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    when (val state = uiState) {
//        is MediaGridUiState.Error -> {
//            ErrorScreen(message = state.message) { state.cause }
//        }
//
//        MediaGridUiState.Loading -> {
//            Text(text = "Loading")
//        }
//
//        is MediaGridUiState.Success -> {
//
//        }
//
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaGrid(
    modifier: Modifier = Modifier,
    viewModel: MediaGridViewModel = hiltViewModel(), // Assuming ViewModel exists
    onMediaClick: (MediaCardUiModel) -> Unit,
    mediaType: AppMediaType,
    mediaSource: MediaSource,
    category: AppCategory,
    onBackClick: () -> Unit
) {
    LaunchedEffect(mediaType, mediaSource, category) {
        viewModel.load(category, mediaSource, mediaType)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = category.title) }, // Dynamic title based on category recommended
                navigationIcon = {
                    // BackButton(onClick = onBackClick) // Implement your BackButton
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = modifier
            .padding(paddingValues)
            .fillMaxSize()) {
            when (val state = uiState) {
                is MediaGridUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        onRetry = { viewModel.retry() } // Assuming ViewModel has retry
                    )
                }

                MediaGridUiState.Loading -> {
                    // Initial Full Screen Loading
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is MediaGridUiState.Success -> {
                    MediaGridSuccessView(
                        data = state.data,
                        onMediaClick = onMediaClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaGridSuccessView(
    modifier: Modifier = Modifier,
    data: MediaGridUiData,
    onMediaClick: (MediaCardUiModel) -> Unit,
) {
    val pagingItems = data.data.collectAsLazyPagingItems()
    val refreshState = pagingItems.loadState.refresh

    // State derivations
    val isRefreshing = refreshState is LoadState.Loading && pagingItems.itemCount > 0
    val isInitialLoading = refreshState is LoadState.Loading && pagingItems.itemCount == 0
    val isError = refreshState is LoadState.Error && pagingItems.itemCount == 0
    val isEmpty = refreshState is LoadState.NotLoading && pagingItems.itemCount == 0

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pagingItems.refresh() },
        modifier = modifier.fillMaxSize()
    ) {

        when {
            isInitialLoading -> {
                // Show Skeleton Grid
                MediaGridSkeleton(columns = data.actualColumns)
            }

            isError -> {
                val error = refreshState.error
                ErrorScreen(
                    message = error.localizedMessage ?: "Unknown Error",
                    onRetry = { pagingItems.retry() }
                )
            }

            isEmpty -> {
                EmptyScreen(message = "No items found in this category.")
            }

            else -> {
                MediaPagedGrid(
                    pagingItems = pagingItems,
                    columns = data.actualColumns,
                    cardStyle = data.cardStyle,
                    onMediaClick = onMediaClick
                )
            }
        }
    }
}

@Composable
fun MediaPagedGrid(
    pagingItems: LazyPagingItems<MediaEntity>,
    columns: Int,
    cardStyle: MediaCardStyle,
    onMediaClick: (MediaCardUiModel) -> Unit
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = gridState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Items
        items(
            count = pagingItems.itemCount,
            key = { index ->
                // Use key if your Entity has stable ID, otherwise use index (less performant)
                val item = pagingItems.peek(index)
                item?.id ?: index
            }
        ) { index ->
            val item = pagingItems[index]
            if (item != null) {
                // Convert Entity to UiModel here or inside the card
                MediaEntityCard(
                    entity = item,
                    mediaStyle = cardStyle,
                    onMediaClick = onMediaClick
                )
            }
        }


        // Append Loading State (Footer)
        if (pagingItems.loadState.append is LoadState.Loading) {
            for (i in 0..columns) {
                item(span = { GridItemSpan(columns) }) {
                    MediaCardShimmer(style = cardStyle)
                }
            }
        }

        // Append Error State (Footer)
        if (pagingItems.loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(columns) }) {
                ErrorFooter(
                    onRetry = { pagingItems.retry() }
                )
            }
        }
    }
}

// --- UI Components ---


@Composable
fun EmptyScreen(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorFooter(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Could not load more items", style = MaterialTheme.typography.labelMedium)
        TextButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
fun MediaGridSkeleton(columns: Int) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(10) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Approximate height
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                MediaCardShimmer(MediaCardStyle.Cover(CoverVariant.MINIMAL))
                // Add shimmer animation here
            }
        }
    }
}


