package com.ghost.bolt.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.SearchFilter
import com.ghost.bolt.enums.SortOption
import com.ghost.bolt.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(SearchFilter())
    val filterState = _filterState.asStateFlow()

    // 🔥 Debounce ONLY query changes
    @OptIn(FlowPreview::class)
    private val debouncedQueryFlow =
        _filterState
            .map { it.query }
            .distinctUntilChanged()
//            .debounce(400)

    // Other filters update instantly
    private val instantFilterFlow =
        _filterState

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<MediaEntity>> =
        combine(debouncedQueryFlow, instantFilterFlow) { _, filter ->
            filter
        }
            .flatMapLatest { filter ->
                repository.searchMediaWithFilter(filter)
            }
            .cachedIn(viewModelScope)

    // ---- Actions ----

    fun onQueryChange(query: String) {
        _filterState.update { it.copy(query = query) }
    }

    fun onMediaTypeChange(type: AppMediaType) {
        _filterState.update { it.copy(mediaType = type) }
    }

}
