package com.ghost.bolt.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.SearchFilter
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCard
import com.ghost.bolt.ui.components.card.MediaCardStyle
import com.ghost.bolt.ui.components.card.MediaEntityCard
import com.ghost.bolt.ui.viewModel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onMediaClick: (MediaCardUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    Scaffold(
        modifier = modifier,
        topBar = {
            SearchTopBar(
                filterState = filterState,
                onQueryChange = viewModel::onQueryChange,
                onMediaTypeChange = viewModel::onMediaTypeChange
            )
        }
    ) { innerPadding ->

        SearchContent(
            modifier = Modifier.padding(innerPadding),
            filterState = filterState,
            searchResults = searchResults,
            onMediaClick = onMediaClick
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchTopBar(
    filterState: SearchFilter,
    onQueryChange: (String) -> Unit,
    onMediaTypeChange: (AppMediaType) -> Unit
) {
    Column {

        val onActiveChange: (Boolean) -> Unit = {}
        val colors1 = SearchBarDefaults.colors()
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = filterState.query,
                    onQueryChange = onQueryChange,
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = { Text("Search movies, anime...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, null) },
                    trailingIcon = {
                        if (filterState.query.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(Icons.Rounded.Clear, "Clear")
                            }
                        }
                    },
                    colors = colors1.inputFieldColors,
                    interactionSource = null,
                )
            },
            expanded = false,
            onExpandedChange = onActiveChange,
            modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = SearchBarDefaults.inputFieldShape,
            colors = colors1,
            tonalElevation = SearchBarDefaults.TonalElevation,
            shadowElevation = SearchBarDefaults.ShadowElevation,
            windowInsets = SearchBarDefaults.windowInsets,
            content = {},
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {

            item {
                FilterChip(
                    selected = false,
                    onClick = { /* TODO */ },
                    label = { Text("Filters") },
                    leadingIcon = {
                        Icon(Icons.Rounded.Edit, null, Modifier.size(16.dp))
                    }
                )
            }

            items(AppMediaType.values()) { type ->
                FilterChip(
                    selected = filterState.mediaType == type,
                    onClick = {
                        val newType =
                            if (filterState.mediaType == type) null else type
                        onMediaTypeChange(newType ?: AppMediaType.MOVIE)
                    },
                    label = { Text(type.name) }
                )
            }
        }
    }
}


@Composable
private fun SearchContent(
    modifier: Modifier = Modifier,
    filterState: SearchFilter,
    searchResults: LazyPagingItems<MediaEntity>,
    onMediaClick: (MediaCardUiModel) -> Unit
) {
    Box(modifier.fillMaxSize()) {

        when {
            searchResults.loadState.refresh is LoadState.Loading -> {
                LoadingState()
            }

            searchResults.itemCount == 0 -> {
                EmptyState(filterState.query)
            }

            else -> {
                SearchGrid(
                    searchResults = searchResults,
                    onMediaClick = onMediaClick
                )
            }
        }
    }
}


@Composable
private fun SearchGrid(
    searchResults: LazyPagingItems<MediaEntity>,
    onMediaClick: (MediaCardUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        items(searchResults.itemCount) { index ->
            searchResults[index]?.let { media ->
                MediaEntityCard(
                    entity = media,
                    mediaStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
                    onMediaClick = onMediaClick
                )
            }
        }

        if (searchResults.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyState(query: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = if (query.isEmpty())
                "Start typing to search..."
            else
                "No results found.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
