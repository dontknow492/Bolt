package com.ghost.bolt.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.DiscoverFilter
import com.ghost.bolt.enums.DiscoverSortOption
import com.ghost.bolt.enums.LogicMode
import com.ghost.bolt.enums.SortOrder
import com.ghost.bolt.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(DiscoverFilter())
    val filterState: StateFlow<DiscoverFilter> = _filterState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val discoverResults: Flow<PagingData<MediaEntity>> =
        _filterState
            .flatMapLatest { filter ->
                repository.discoverMedia(filter)
            }
            .onEach {
                Timber.tag("Discover Media").v("New paging data received: ${_filterState.value}")
            }
            .cachedIn(viewModelScope)

    // -----------------------
    // 🔄 Filter Actions
    // -----------------------

    fun setMediaType(type: AppMediaType) {
        _filterState.update { it.copy(mediaType = type) }
    }

    fun toggleGenre(id: Int) {
        _filterState.update { current ->
            val updated = current.genres.toMutableSet()
            if (updated.contains(id)) updated.remove(id)
            else updated.add(id)

            current.copy(genres = updated)
        }
    }

    fun setGenreLogic(mode: LogicMode) {
        _filterState.update { it.copy(genreLogic = mode) }
    }

    fun toggleCast(id: Int) {
        _filterState.update { current ->
            val updated = current.cast.toMutableSet()
            if (updated.contains(id)) updated.remove(id)
            else updated.add(id)

            current.copy(cast = updated)
        }
    }

    fun toggleKeyword(id: Int) {
        _filterState.update { current ->
            val updated = current.keywords.toMutableSet()
            if (updated.contains(id)) updated.remove(id)
            else updated.add(id)

            current.copy(keywords = updated)
        }
    }

    fun setYearRange(min: Int?, max: Int?) {
        _filterState.update {
            it.copy(
                minYear = min,
                maxYear = max
            )
        }
    }

    fun setVoteRange(min: Float?, max: Float?) {
        _filterState.update {
            it.copy(
                minVote = min,
                maxVote = max
            )
        }
    }

    fun setRuntimeRange(min: Int?, max: Int?) {
        _filterState.update {
            it.copy(
                minRuntime = min,
                maxRuntime = max
            )
        }
    }

    fun setSort(option: DiscoverSortOption, order: SortOrder) {
        _filterState.update {
            it.copy(
                sortOption = option,
                sortOrder = order
            )
        }
    }

    fun setIncludeAdult(include: Boolean) {
        _filterState.update {
            it.copy(includeAdult = include)
        }
    }

    fun clearFilters() {
        _filterState.value = DiscoverFilter(
            mediaType = _filterState.value.mediaType // keep current type
        )
    }
}
