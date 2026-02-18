package com.ghost.bolt.api.tmdb.response

import com.ghost.bolt.api.tmdb.response.fields.NetworkBelongsToCollection
import com.ghost.bolt.api.tmdb.response.fields.NetworkCredits
import com.ghost.bolt.api.tmdb.response.fields.NetworkGenre
import com.ghost.bolt.api.tmdb.response.fields.NetworkKeywords
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCompany
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCountry
import com.ghost.bolt.api.tmdb.response.fields.NetworkSpokenLanguage
import com.ghost.bolt.api.tmdb.response.fields.NetworkVideos
import com.ghost.models.BaseMediaDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a Movie object from the Network.
 * Covers both list-view fields (lighter) and detail-view fields (budget, revenue, etc).
 */

// --- Helper Models for Appended Data ---


@Serializable
data class TMDbMovieDetailResponse(
    // --- Common Fields Implemented ---
    @SerialName("id") override val id: Int,
    @SerialName("adult") override val adult: Boolean? = null,
    @SerialName("backdrop_path") override val backdropPath: String? = null,
    @SerialName("poster_path") override val posterPath: String? = null,
    @SerialName("overview") override val overview: String? = null,
    @SerialName("original_language") override val originalLanguage: String? = null,
    @SerialName("popularity") override val popularity: Double? = null,
    @SerialName("vote_average") override val voteAverage: Double? = null,
    @SerialName("vote_count") override val voteCount: Int? = null,
    @SerialName("status") override val status: String? = null,
    @SerialName("tagline") override val tagline: String? = null,
    @SerialName("homepage") override val homepage: String? = null,

    @SerialName("genres") override val genres: List<NetworkGenre>? = null,
    @SerialName("production_companies") override val productionCompanies: List<NetworkProductionCompany>? = null,
    @SerialName("production_countries") override val productionCountries: List<NetworkProductionCountry>? = null,
    @SerialName("spoken_languages") override val spokenLanguages: List<NetworkSpokenLanguage>? = null,
    @SerialName("keywords") override val keywords: NetworkKeywords? = null,

    @SerialName("credits") override val credits: NetworkCredits? = null,
    @SerialName("videos") override val videos: NetworkVideos? = null,

    // --- Movie Specific Fields ---
    @SerialName("title") val title: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("video") val video: Boolean? = null,
    @SerialName("budget") val budget: Long? = null,
    @SerialName("revenue") val revenue: Long? = null,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("belongs_to_collection") val belongsToCollection: NetworkBelongsToCollection? = null,

    // Self-referencing generic for similar/recommendations
    @SerialName("similar") val similar: TmdbResponse<TMDbMovie>? = null,
    @SerialName("recommendations") val recommendations: TmdbResponse<TMDbMovie>? = null
) : BaseMediaDetail()



