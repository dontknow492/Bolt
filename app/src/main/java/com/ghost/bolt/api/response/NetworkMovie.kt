package com.ghost.bolt.api.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Movie object from the Network.
 * Covers both list-view fields (lighter) and detail-view fields (budget, revenue, etc).
 */
@Serializable
data class NetworkMovie(
    // List & Basic Fields
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    @SerialName("vote_count") val voteCount: Int? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("popularity") val popularity: Double? = null,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("adult") val adult: Boolean? = null,
    @SerialName("video") val video: Boolean? = null,
    @SerialName("genre_ids") val genreIds: List<Int>? = null,

    // Detail Fields
    @SerialName("budget") val budget: Long? = null,
    @SerialName("revenue") val revenue: Long? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("imdb_id") val imdbId: String? = null,

    // --- APPENDED DATA (From append_to_response) ---
    // These are nullable because they won't exist in simple "Popular" list responses

    @SerialName("credits") val credits: NetworkCredits? = null,

    @SerialName("videos") val videos: NetworkVideos? = null,

    // "Similar" and "Recommendations" return a standard list of movies, so we reuse TmdbResponse
    @SerialName("similar") val similar: TmdbResponse? = null,
    @SerialName("recommendations") val recommendations: TmdbResponse? = null
)


// --- Helper Models for Appended Data ---

