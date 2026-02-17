package com.ghost.bolt.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCard
import com.ghost.bolt.ui.components.card.MediaCardShimmer
import com.ghost.bolt.ui.components.card.MediaCardStyle
import com.ghost.bolt.ui.viewModel.HomeUiData
import com.ghost.bolt.ui.viewModel.HomeUiState
import com.ghost.bolt.ui.viewModel.HomeViewModel
import com.ghost.bolt.utils.TmdbConfig
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    mediaType: AppMediaType,
    modifier: Modifier = Modifier
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
            HomeSuccessView(
                modifier = modifier.fillMaxSize(),
                data = state.data,
                onMediaClick = onMediaClick
            )
        }

    }

    LaunchedEffect(mediaType) {
        Timber.tag("Home Screen").v("Loading media type: $mediaType")
        viewModel.load(mediaType)
    }
}

@Composable
fun HomeSuccessView(
    modifier: Modifier = Modifier,
    data: HomeUiData,
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
) {
    val topRated = data.topRated.collectAsLazyPagingItems()
    val trending = data.trending.collectAsLazyPagingItems()
    val popular = data.popular.collectAsLazyPagingItems()
    val upcoming = data.upcoming.collectAsLazyPagingItems()


    SharedTransitionLayout {
        AnimatedVisibility(true) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    MediaHorizontalSection(
                        title = "Trending Now",
                        data = trending,
                        mediaStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
                        animatedContentScope = this@AnimatedVisibility,
                        onSeeAllClick = { },
                        onMediaClick = onMediaClick
                    )
                }

                item {
                    MediaHorizontalSection(
                        title = "Popular",
                        data = popular,
                        mediaStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
                        animatedContentScope = this@AnimatedVisibility,
                        onSeeAllClick = { },
                        onMediaClick = onMediaClick
                    )
                }

                item {
                    MediaHorizontalSection(
                        title = "Coming Soon",
                        data = upcoming,
                        mediaStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
                        animatedContentScope = this@AnimatedVisibility,
                        onSeeAllClick = { },
                        onMediaClick = onMediaClick
                    )
                }

                stickyHeader {
                    SectionHeader(
                        title = "Top Rated",
                        onSeeAllClick = null,
                        modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    )
                }

                items(
                    topRated.itemCount,
                    key = { index -> topRated[index]?.id ?: index }) { index ->
                    val entity = topRated[index]
                    if (entity != null) {
                        Entity(
                            entity = entity,
                            mediaStyle = MediaCardStyle.List,
                            modifier = Modifier,
                            onMediaClick = onMediaClick,
                            animatedContentScope = this@AnimatedVisibility
                        )
                    }

                }

            }
        }

    }
}


@Composable
fun SharedTransitionScope.MediaHorizontalSection(
    modifier: Modifier = Modifier,
    title: String,
    data: LazyPagingItems<MediaEntity>,
    mediaStyle: MediaCardStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
    animatedContentScope: AnimatedVisibilityScope,
    onSeeAllClick: () -> Unit,
    onRetry: () -> Unit = { data.retry() },
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
) {

    val refreshState = data.loadState.refresh
    val isLoading = refreshState is LoadState.Loading
    val isError = refreshState is LoadState.Error
    val error_message = (refreshState as? LoadState.Error)?.error?.message
    val isEmpty = data.itemCount == 0 && refreshState is LoadState.NotLoading

    // 🚫 Hide whole section if empty
    if (isEmpty) return

    Column(modifier = modifier) {

        SectionHeader(
            title = title,
            isLoading = isLoading,
            isError = isError,
            showSeeAll = data.itemCount > 0,
            onSeeAllClick = if (data.itemCount > 0) onSeeAllClick else null
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {

            // 🔄 Loading shimmer
            isLoading -> {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(6) {
                        MediaCardShimmer(style = mediaStyle)
                    }
                }
            }

            // ❌ Error state
            isError -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Failed to load: $error_message",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )

                    Button(onClick = onRetry) {
                        Text("Retry")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                    }
                }
            }

            // ✅ Success
            else -> {
                val limit = 20
                val displayCount = minOf(data.itemCount, limit)
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        count = displayCount,
                        // Use the same key logic
                        key = { index -> data[index]?.id ?: index }
                    ) { index ->

                        val entity = data[index]

                        if (entity != null) {
                            Entity(
                                entity = entity,
                                mediaStyle = mediaStyle,
                                modifier = Modifier,
                                onMediaClick = onMediaClick,
                                animatedContentScope = animatedContentScope
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedTransitionScope.Entity(
    entity: MediaEntity,
    mediaStyle: MediaCardStyle,
    modifier: Modifier = Modifier,
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    animatedContentScope: AnimatedVisibilityScope
) {
    val posterUrl = TmdbConfig.getPosterUrl(entity.posterPath, TmdbConfig.TMDbPosterSize.W342)
    val backdropUrl = TmdbConfig.getBackdropUrl(entity.backdropPath)
    MediaCard(
        modifier = modifier,
        mediaId = entity.id,
        title = entity.title,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = entity.voteAverage,
        voteCount = entity.voteCount,
        overview = entity.overview,
        releaseDate = formatTimestampToDate(entity.releaseDate),
        mediaType = entity.mediaType,
        style = mediaStyle,
        onMediaClick = onMediaClick,
        animatedVisibilityScope = animatedContentScope,
    )
}

@Composable
fun SectionHeader(
    modifier: Modifier = Modifier,
    title: String,
    isLoading: Boolean = false,
    isError: Boolean = false,
    showSeeAll: Boolean = true,
    onSeeAllClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            }

            isError -> {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            showSeeAll && onSeeAllClick != null -> {
                TextButton(onClick = onSeeAllClick) {
                    Text("See All")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null
                    )
                }
            }
        }
    }
}


fun formatTimestampToDate(timestamp: Long?): String? {
    timestamp ?: return null
    // Create a date object from the timestamp
    val date = Date(timestamp)

    // Create a SimpleDateFormat for formatting the date
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Return the formatted date string
    return dateFormat.format(date)
}
