package com.ghost.bolt.api.tmdb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDbTrendingItem(
    @SerialName("media_type") val mediaType: String,
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    @SerialName("poster_path") val posterPath: String? = null
)