package com.ghost.bolt.utils.mapper

import com.ghost.bolt.api.response.NetworkMovie
import com.ghost.bolt.database.Converters
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.Status


/**
 * Maps the TMDB SDK 'MovieDb' object to your local 'MediaEntity'.
 */
fun NetworkMovie.toMediaEntity(): MediaEntity {
    // 1. Create an instance of your converter to reuse the date parsing logic
    val converters = Converters()

    val statusEnum = if (this.status != null) {
        converters.fromStatusString(this.status)
    } else {
        Status.RELEASED // Safe default for popular lists
    }

    // 2. Handle the Date Parsing safely
    // The SDK returns dates as strings (YYYY-MM-DD), so we convert to Long timestamp
    val releaseDateTimestamp = converters.fromSqlDateStringToLong(this.releaseDate)


    return MediaEntity(
        id = this.id,
        title = this.title ?: "",
        overview = this.overview ?: "",
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,

        // Convert Numbers safely
        voteAverage = this.voteAverage?.toFloat(), // Float
        voteCount = this.voteCount,     // Int
        popularity = this.popularity?.toFloat(),   // Float

        // Dates
        releaseDate = releaseDateTimestamp,
        finishDate = null,

        // Enums & Types
        mediaType = AppMediaType.MOVIE,

        // Metadata that might be null in a simple list fetch
        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle,

        // Financials (Might be 0/null in list view)
        adult = this.adult,

        status = statusEnum,
        homepage = this.homepage,
        imdbId = this.imdbId,
        tagline = this.tagline,
        revenue = this.revenue,
        runtime = this.runtime,
        budget = this.budget,
        episodes = 0,
    )
}
