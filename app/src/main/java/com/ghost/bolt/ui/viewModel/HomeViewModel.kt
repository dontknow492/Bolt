package com.ghost.bolt.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiData(
    val popularMovies: Flow<PagingData<MediaEntity>>,
    val topRatedMovies: Flow<PagingData<MediaEntity>>,
    val trendingMovies: Flow<PagingData<MediaEntity>>,
    val upcomingMovies: Flow<PagingData<MediaEntity>>,
)

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Error(val message: String, val cause: Throwable?) : HomeUiState()
    data class Success(val data: HomeUiData) : HomeUiState()
}


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MediaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun load(mediaType: AppMediaType) {
        try {
            _uiState.update {
                HomeUiState.Success(
                    HomeUiData(
                        popularMovies = repository
                            .getCategoryMediaList(AppCategory.POPULAR, mediaType)
                            .cachedIn(viewModelScope),

                        topRatedMovies = repository
                            .getCategoryMediaList(AppCategory.TOP_RATED, mediaType)
                            .cachedIn(viewModelScope),

                        trendingMovies = repository
                            .getCategoryMediaList(AppCategory.TRENDING, mediaType)
                            .cachedIn(viewModelScope),

                        upcomingMovies = repository
                            .getCategoryMediaList(AppCategory.UPCOMING, mediaType)
                            .cachedIn(viewModelScope),
                    )
                )
            }
        } catch (e: Exception) {
            _uiState.update { HomeUiState.Error(e.message ?: "Unknown error", e) }
        }

    }
}
