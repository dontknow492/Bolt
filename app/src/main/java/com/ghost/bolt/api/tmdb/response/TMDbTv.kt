package com.ghost.bolt.api.tmdb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDbTv(

    override val id: Int,
    override val overview: String? = null,
    @SerialName("poster_path")
    override val posterPath: String? = null,
    @SerialName("backdrop_path")
    override val backdropPath: String? = null,
    @SerialName("vote_average")
    override val voteAverage: Double? = null,
    @SerialName("vote_count")
    override val voteCount: Int? = null,
    override val popularity: Double? = null,
    @SerialName("original_language")
    override val originalLanguage: String? = null,
    override val adult: Boolean? = null,

    // TV specific
    val name: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("original_name") val originalName: String? = null,

    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null,
    @SerialName("in_production") val inProduction: Boolean? = null,
    @SerialName("last_air_date") val lastAirDate: String? = null
) : TMDbBaseMedia