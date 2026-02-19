package com.ghost.bolt.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ghost.bolt.R
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.DiscoverFilter
import com.ghost.bolt.enums.DiscoverSortOption
import com.ghost.bolt.enums.SortOrder
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCardShimmer
import com.ghost.bolt.ui.components.card.MediaCardStyle
import com.ghost.bolt.ui.components.card.MediaEntityCard
import com.ghost.bolt.ui.viewModel.DiscoverViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    onMediaClick: (MediaCardUiModel) -> Unit
) {
    val filter by viewModel.filterState.collectAsState()
    val pagingItems = viewModel.discoverResults.collectAsLazyPagingItems()
    var bottomSheetVisible: Boolean by remember { mutableStateOf(false) }

//    val pagingItems = viewModel.discoverResults.collectAsLazyPagingItems()

// Add this temporary log
    LaunchedEffect(pagingItems.loadState) {
        Timber.d("UI LoadState: ${pagingItems.loadState.refresh}, Count: ${pagingItems.itemCount}")
    }


    Scaffold(
        modifier = modifier,
        topBar = {
            DiscoverTopBar(
                filter = filter,
                onMediaTypeChange = viewModel::setMediaType,
                onFilterClick = { bottomSheetVisible = true }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            Text("${pagingItems.itemCount} results, ")


            DiscoverResultsGrid(
                pagingItems, MediaCardStyle.Cover(CoverVariant.NORMAL),
                onMediaClick = onMediaClick
            )
        }
    }

    AnimatedVisibility(bottomSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { bottomSheetVisible = false },
        ) {
            DiscoverFilters(
                filter = filter,
                onGenreToggle = viewModel::toggleGenre,
                onSortChange = viewModel::setSort,
                onVoteChange = viewModel::setVoteRange,
                onAdultToggle = viewModel::setIncludeAdult,
                onClearClick = viewModel::clearFilters
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverTopBar(
    filter: DiscoverFilter,
    onMediaTypeChange: (AppMediaType) -> Unit,
    onFilterClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Discover") },
        actions = {

            MediaTypeToggle(
                selected = filter.mediaType,
                onSelected = onMediaTypeChange
            )
            IconButton(onClick = onFilterClick) {
                Icon(painterResource(R.drawable.discover_tune), contentDescription = "filter list")
            }


        }
    )
}

@Composable
fun MediaTypeToggle(
    selected: AppMediaType,
    onSelected: (AppMediaType) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        FilterChip(
            selected = selected == AppMediaType.MOVIE,
            onClick = { onSelected(AppMediaType.MOVIE) },
            label = { Text("Movies") }
        )

        FilterChip(
            selected = selected == AppMediaType.TV,
            onClick = { onSelected(AppMediaType.TV) },
            label = { Text("TV") }
        )
    }
}


@Composable
fun DiscoverFilters(
    filter: DiscoverFilter,
    onGenreToggle: (Int) -> Unit,
    onSortChange: (DiscoverSortOption, SortOrder) -> Unit,
    onVoteChange: (Float?, Float?) -> Unit,
    onAdultToggle: (Boolean) -> Unit,
    onClearClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        GenreSection(filter, onGenreToggle)

        SortSection(filter, onSortChange)

        RatingSection(filter, onVoteChange)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Include Adult")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = filter.includeAdult,
                onCheckedChange = onAdultToggle
            )
        }
        Button(onClick = onClearClick) {
            Text("Clear Filters")
            Icon(Icons.Default.Refresh, contentDescription = "Clear Filters")
        }
    }
}

@Composable
fun GenreSection(
    filter: DiscoverFilter,
    onToggle: (Int) -> Unit
) {

    val sampleGenres = listOf(
        28 to "Action",
        35 to "Comedy",
        18 to "Drama",
        27 to "Horror"
    )

    Text("Genres", style = MaterialTheme.typography.titleMedium)

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sampleGenres.forEach { (id, name) ->
            FilterChip(
                selected = filter.genres.contains(id),
                onClick = { onToggle(id) },
                label = { Text(name) }
            )
        }
    }
}


@Composable
fun SortSection(
    filter: DiscoverFilter,
    onSortChange: (DiscoverSortOption, SortOrder) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    Box {

        OutlinedButton(onClick = { expanded = true }) {
            Text("Sort: ${filter.sortOption.name}")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DiscoverSortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    onClick = {
                        onSortChange(option, SortOrder.DESC)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun RatingSection(
    filter: DiscoverFilter,
    onVoteChange: (Float?, Float?) -> Unit
) {

    var sliderPosition by remember {
        mutableStateOf(filter.minVote ?: 0f)
    }

    Column {
        Text("Minimum Rating: ${sliderPosition.toInt()}")

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                onVoteChange(it, null)
            },
            valueRange = 0f..10f
        )
    }
}


//import androidx.paging.compose.items // Ensure this import is present

@Composable
fun DiscoverResultsGrid(
    items: LazyPagingItems<MediaEntity>,
    mediaStyle: MediaCardStyle,
    onMediaClick: (MediaCardUiModel) -> Unit,
    columns: Int = 1,
    // ... other params
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        // ... styling
    ) {
        // ✅ Use the items extension, not items(count)
        items(
            count = items.itemCount,
            key = { index -> items[index]?.id ?: index } // Better performance with keys
        ) { index ->
            items[index]?.let { media ->
                MediaEntityCard(
                    entity = media,
                    onMediaClick = onMediaClick,
                    mediaStyle = mediaStyle
                )
            }
        }

        // Handle Loading/Error states
        if (items.loadState.append is LoadState.Loading) {
            item { MediaCardShimmer(mediaStyle) }
        }
    }
}
