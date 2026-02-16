package com.ghost.bolt.utils.mapper

import com.ghost.bolt.api.response.TMDbNetworkMedia
import com.ghost.bolt.database.Converters
import com.ghost.bolt.database.entity.MediaEntity


/**
 * Maps the TMDB SDK 'MovieDb' object to your local 'MediaEntity'.
 */
fun TMDbNetworkMedia.toMediaEntity(): MediaEntity {
    val converters = Converters()

    return MediaEntity(
        id = this.id,
        // Ensure this is NEVER null to satisfy Room @PrimaryKey / Non-null constraints
        title = this.displayTitle,

        overview = this.overview ?: "",
        posterPath = this.posterPath,
        backdropPath = this.backdropPath,

        voteAverage = this.voteAverage?.toFloat(),
        voteCount = this.voteCount,
        popularity = this.popularity?.toFloat(),

        // Use the Long helpers we discussed
        releaseDate = this.releaseDateLong,
        finishDate = this.lastAirDateLong,

        // Use the getter we defined
        mediaType = this.mediaType,

        originalLanguage = this.originalLanguage,
        originalTitle = this.originalTitle ?: this.originalName, // Add TV support here

        adult = this.adult,
        status = this.status?.let { converters.fromStatusString(it) },

        homepage = this.homepage,
        imdbId = this.imdbId,
        tagline = this.tagline,
        revenue = this.revenue,
        runtime = this.runtime,
        budget = this.budget,
        episodes = this.numberOfEpisodes, // Only populated in TV detail views
        themeColor = null
    )
}
