package com.ghost.bolt.ui.viewModel

import androidx.lifecycle.ViewModel
import com.ghost.bolt.data.store.ApiKeyStore
import com.ghost.bolt.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val apiKeyStore: ApiKeyStore,
    private val repository: MediaRepository // Injecting this ensures caches are cleared if needed
) : ViewModel() {

    fun onApiKeyChanged(newKey: String) {
        apiKeyStore.setUserApiKey(newKey)
    }
}