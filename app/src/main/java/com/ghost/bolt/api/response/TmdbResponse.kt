package com.ghost.bolt.api.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generic response wrapper for TMDB list endpoints (Popular, Top Rated, etc.)
 */
@Serializable
data class TmdbResponse(
    @SerialName("dates")val dates: TMDbDates? = null,
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<TMDbNetworkMedia>,
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)

