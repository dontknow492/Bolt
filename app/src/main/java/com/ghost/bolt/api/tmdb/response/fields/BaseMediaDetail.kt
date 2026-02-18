package com.ghost.models

import com.ghost.bolt.api.tmdb.response.fields.NetworkCredits
import com.ghost.bolt.api.tmdb.response.fields.NetworkGenre
import com.ghost.bolt.api.tmdb.response.fields.NetworkKeywords
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCompany
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCountry
import com.ghost.bolt.api.tmdb.response.fields.NetworkSpokenLanguage
import com.ghost.bolt.api.tmdb.response.fields.NetworkVideos
import kotlinx.serialization.Serializable

/**
 * Base abstract class containing common fields shared between Movies and TV Shows
 * based on TMDb API responses.
 * * Note: We use abstract properties here. In Kotlin data classes, you must override
 * these in the constructor to ensure they are serialized correctly and included
 * in methods like copy() and toString().
 */
@Serializable
abstract class BaseMediaDetail {
    abstract val id: Int
    abstract val adult: Boolean?
    abstract val backdropPath: String?
    abstract val posterPath: String?
    abstract val overview: String?
    abstract val originalLanguage: String?
    abstract val popularity: Double?
    abstract val voteAverage: Double?
    abstract val voteCount: Int?
    abstract val status: String?
    abstract val tagline: String?
    abstract val homepage: String?

    // Common Collections
    abstract val genres: List<NetworkGenre>?
    abstract val productionCompanies: List<NetworkProductionCompany>?
    abstract val productionCountries: List<NetworkProductionCountry>?
    abstract val spokenLanguages: List<NetworkSpokenLanguage>?
    abstract val keywords: NetworkKeywords?

    // Common Appendages
    abstract val credits: NetworkCredits?
    abstract val videos: NetworkVideos?
}
