package com.ghost.bolt.api.tmdb.response

import com.ghost.bolt.api.tmdb.response.fields.NetworkCredits
import com.ghost.bolt.api.tmdb.response.fields.NetworkGenre
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCompany
import com.ghost.bolt.api.tmdb.response.fields.NetworkProductionCountry
import com.ghost.bolt.api.tmdb.response.fields.NetworkSpokenLanguage
import com.ghost.bolt.api.tmdb.response.fields.NetworkVideos

interface TMDbBaseMedia {
    val id: Int
    val overview: String?
    val posterPath: String?
    val backdropPath: String?
    val voteAverage: Double?
    val voteCount: Int?
    val popularity: Double?
    val originalLanguage: String?
    val adult: Boolean?
}

interface TMDbBaseDetailedMedia {
    var id: Int?
    var adult: Boolean?
    var backdropPath: String?
    var posterPath: String?
    var overview: String?
    var originalLanguage: String?
    var popularity: Double?
    var voteAverage: Double?
    var voteCount: Int?
    var status: String?
    var tagline: String?
    var homepage: String?

    // Collections
    var genres: List<NetworkGenre>
    var productionCompanies: List<NetworkProductionCompany>
    var productionCountries: List<NetworkProductionCountry>
    var spokenLanguages: List<NetworkSpokenLanguage>

    // Media Appendages
    var credits: NetworkCredits?
    var recommendations: TmdbResponse<TMDbBaseMedia>??
    var videos: NetworkVideos?
}