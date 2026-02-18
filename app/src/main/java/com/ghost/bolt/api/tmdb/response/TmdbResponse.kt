package com.ghost.bolt.api.tmdb.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic response wrapper for TMDB list endpoints (Popular, Top Rated, etc.)
 */
@Serializable
data class TmdbResponse<T>(
    @SerialName("dates") val dates: com.ghost.bolt.api.tmdb.response.fields.TMDbDates? = null,
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<T>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)


