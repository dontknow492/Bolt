package com.ghost.bolt.ui.viewModel

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghost.bolt.R
import com.ghost.bolt.models.UiMediaDetail
import com.ghost.bolt.repository.MediaRepository
import com.ghost.bolt.ui.components.loadImageBitmapFromUrl
import com.ghost.bolt.ui.screen.detail.BackdropSource
import com.ghost.bolt.ui.theme.SeedColor
import com.ghost.bolt.utils.ThemeColorCache
import com.ghost.bolt.utils.TmdbConfig
import com.materialkolor.ktx.themeColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed interface DetailUiState {
    data class Loading(val backdropPath: BackdropSource = BackdropSource.Local(R.drawable.error_image_placeholder)) :
        DetailUiState

    data class Error(val message: String, val throwable: Throwable?) : DetailUiState
    data class Success(val data: UiMediaDetail) : DetailUiState
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: MediaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()


    fun loadDetailData(id: Int, coverPath: String?, title: String?, backdropPath: String?) {
        Timber.tag("DetailViewModel")
            .v("Loading detail data for ID: %d", id, coverPath, title, backdropPath)
        if (coverPath != null) {
            _uiState.update {
                DetailUiState.Loading(BackdropSource.Network(TmdbConfig.getBackdropUri(coverPath)))
            }
        }
        viewModelScope.launch {
            repository.getUiMediaDetail(id)
                .onStart {
                    _uiState.value = DetailUiState.Loading()
                }
                .catch { throwable ->
                    _uiState.value = DetailUiState.Error(
                        throwable.message ?: "Something went wrong", throwable
                    )
                }
                .collect { detail ->
                    if (detail != null) {
                        _uiState.value = DetailUiState.Success(detail)
                    } else {
                        _uiState.value = DetailUiState.Error("Media not found", null)
                    }
                }

//            refreshData(id = id)
        }


    }

    fun refreshData(id: Int) {
        viewModelScope.launch {
            repository.refreshMediaDetail(id)
        }
    }
}


@HiltViewModel
class DynamicThemeViewModel @Inject constructor() : ViewModel() {
    private val _seedColor = MutableStateFlow(SeedColor)
    val seedColor: StateFlow<Color> = _seedColor.asStateFlow()

    fun loadThemeColor(imageUrl: String, context: Context) {

        // 1️⃣ Check cache first
        ThemeColorCache.get(imageUrl)?.let {
            _seedColor.value = it
            return
        }

        // 2️⃣ If not cached → calculate
        viewModelScope.launch(Dispatchers.IO) {

            val bitmap = loadImageBitmapFromUrl(context, imageUrl)
            val color = bitmap?.themeColor(SeedColor) ?: SeedColor

            // Save in cache
            ThemeColorCache.put(imageUrl, color)

            _seedColor.value = color
        }
    }
}
