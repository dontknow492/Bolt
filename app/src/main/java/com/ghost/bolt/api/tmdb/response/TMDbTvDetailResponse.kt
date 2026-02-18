package com.ghost.bolt.api.tmdb.response


import com.ghost.bolt.api.tmdb.response.fields.CreatedBy
import com.ghost.bolt.api.tmdb.response.fields.NetworkCredits
import com.ghost.bolt.api.tmdb.response.fields.NetworkGenre
import com.ghost.bolt.api.tmdb.response.fields.NetworkKeywords
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCompany
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCountry
import com.ghost.bolt.api.tmdb.response.fields.NetworkSpokenLanguage
import com.ghost.bolt.api.tmdb.response.fields.NetworkVideos
import com.ghost.bolt.api.tmdb.response.fields.Seasons
import com.ghost.bolt.api.tmdb.response.fields.TMDbEpisode
import com.ghost.bolt.api.tmdb.response.fields.TMDbNetworks
import com.ghost.models.BaseMediaDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class TMDbTvDetailResponse(
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

    // --- TV Specific Fields ---
    @SerialName("name") val name: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("last_air_date") val lastAirDate: String? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null,
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerialName("episode_run_time") val episodeRunTime: List<Int>? = null,
    @SerialName("in_production") val inProduction: Boolean? = null,
    @SerialName("languages") val languages: List<String>? = null,
    @SerialName("origin_country") val originCountry: List<String>? = null,
    @SerialName("created_by") val createdBy: List<CreatedBy>? = null,
    @SerialName("networks") val networks: List<TMDbNetworks>? = null,
    @SerialName("seasons") val seasons: List<Seasons>? = null,
    @SerialName("type") val type: String? = null,

    // Episode types
    @SerialName("last_episode_to_air") val lastEpisodeToAir: TMDbEpisode? = null,
    @SerialName("next_episode_to_air") val nextEpisodeToAir: TMDbEpisode? = null,

    // Self-referencing generic for similar/recommendations
    @SerialName("similar") val similar: TmdbResponse<TMDbTv>? = null,
    @SerialName("recommendations") val recommendations: TmdbResponse<TMDbTv>? = null
) : BaseMediaDetail()


