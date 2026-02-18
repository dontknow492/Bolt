package com.ghost.bolt.api.tmdb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDbMovie(

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


    // Movie specific
    val title: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    val video: Boolean? = null,

    // Detail fields
    val budget: Long? = null,
    val revenue: Long? = null,
    val runtime: Int? = null,
    val status: String? = null,
    val tagline: String? = null,
    val homepage: String? = null,

    ) : TMDbBaseMedia