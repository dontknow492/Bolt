package com.ghost.bolt.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.repository.MediaRepository
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCardStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class MediaGridUiData(
    val category: AppCategory,
    val mediaSource: MediaSource,
    val mediaType: AppMediaType,
    val data: Flow<PagingData<MediaEntity>>,
    val userPreferredColumns: Int = 2, // Renamed for clarity
    val cardStyle: MediaCardStyle = MediaCardStyle.Cover(CoverVariant.NORMAL)
) {
    // Logic: Force 1 column for Detailed or List styles, otherwise use user preference
    val actualColumns: Int
        get() = when (cardStyle) {
            is MediaCardStyle.Detailed, is MediaCardStyle.List -> 1
            is MediaCardStyle.Cover -> userPreferredColumns
        }
}

sealed interface MediaGridUiState {
    object Loading : MediaGridUiState
    data class Error(val message: String, val cause: Throwable?) : MediaGridUiState
    data class Success(val data: MediaGridUiData) : MediaGridUiState
}


@HiltViewModel
class MediaGridViewModel @Inject constructor(
    private val repository: MediaRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MediaGridUiState>(MediaGridUiState.Loading)

    val uiState: StateFlow<MediaGridUiState> = _uiState.asStateFlow()

//    private val _isRefreshing = MutableStateFlow(false)
//    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private lateinit var category: AppCategory
    private lateinit var mediaSource: MediaSource
    private lateinit var mediaType: AppMediaType

    fun load(
        category: AppCategory,
        mediaSource: MediaSource,
        mediaType: AppMediaType
    ) {
        this.category = category
        this.mediaSource = mediaSource
        this.mediaType = mediaType


        try {
            _uiState.update {
                MediaGridUiState.Success(
                    MediaGridUiData(
                        data = repository.getCategoryMediaList(category, mediaType, mediaSource)
                            .cachedIn(viewModelScope),
                        category = category,
                        mediaSource = mediaSource,
                        mediaType = mediaType
                    )
                )
            }
            this.mediaType = mediaType
        } catch (e: Exception) {
            _uiState.update { MediaGridUiState.Error(e.message ?: "Unknown error", e) }
        }

    }

    fun retry() {
        load(category, mediaSource, mediaType)
    }

}