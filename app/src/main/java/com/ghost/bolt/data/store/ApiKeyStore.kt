package com.ghost.bolt.data.store

import com.ghost.bolt.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyStore @Inject constructor() {

    // Volatile ensures thread safety when reading/writing from different threads
    @Volatile
    private var _userApiKey: String? = null

    val apiKey: String
        get() = if (!_userApiKey.isNullOrBlank()) {
            _userApiKey!!
        } else {
            // Fallback to the key in your local.properties / build.gradle
            BuildConfig.TMDB_API_KEY
        }

    fun setUserApiKey(key: String) {
        _userApiKey = key
    }

    fun resetToDefault() {
        _userApiKey = null
    }
}