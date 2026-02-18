package com.ghost.bolt.utils.mapper


import com.ghost.bolt.api.tmdb.response.TMDbMovie
import com.ghost.bolt.api.tmdb.response.TMDbMovieDetailResponse
import com.ghost.bolt.api.tmdb.response.TMDbTv
import com.ghost.bolt.api.tmdb.response.TMDbTvDetailResponse
import com.ghost.bolt.database.Converters
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import java.time.LocalDate.parse
import java.time.ZoneId


fun String.toEpochMillis(): Long? {
    return try {
        parse(this)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        null
    }
}


fun TMDbMovie.toMediaEntity(): MediaEntity {
    return MediaEntity(
        id = id,
        title = title ?: "Unknown",

        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,

        voteAverage = voteAverage?.toFloat(),
        voteCount = voteCount,
        popularity = popularity?.toFloat(),

        releaseDate = releaseDate?.toEpochMillis(),
        finishDate = null,

        mediaType = AppMediaType.MOVIE,

        originalLanguage = originalLanguage,
        originalTitle = originalTitle,

        adult = adult,
        status = null, // not available in list endpoint

        homepage = null,
        imdbId = null,
        tagline = null,
        revenue = null,
        runtime = null,
        budget = null,
        episodes = null,

        themeColor = null,
        mediaSource = MediaSource.TMDB
    )
}


fun TMDbTv.toMediaEntity(): MediaEntity {
    return MediaEntity(
        id = id,
        title = name ?: "Unknown",

        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,

        voteAverage = voteAverage?.toFloat(),
        voteCount = voteCount,
        popularity = popularity?.toFloat(),

        releaseDate = firstAirDate?.toEpochMillis(),
        finishDate = null,

        mediaType = AppMediaType.TV,

        originalLanguage = originalLanguage,
        originalTitle = originalName,

        adult = adult,
        status = null,

        homepage = null,
        imdbId = null,
        tagline = null,
        revenue = null,
        runtime = null,
        budget = null,
        episodes = null,

        themeColor = null,
        mediaSource = MediaSource.TMDB
    )
}


fun TMDbMovieDetailResponse.toMediaEntity(
    converters: Converters
): MediaEntity {
    return MediaEntity(
        id = id,
        title = title ?: "Unknown",

        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,

        voteAverage = voteAverage?.toFloat(),
        voteCount = voteCount,
        popularity = popularity?.toFloat(),

        releaseDate = releaseDate?.toEpochMillis(),
        finishDate = null,

        mediaType = AppMediaType.MOVIE,

        originalLanguage = originalLanguage,
        originalTitle = originalTitle,

        adult = adult,
        status = status?.let { converters.fromStatusString(it) },

        homepage = homepage,
        imdbId = imdbId,
        tagline = tagline,
        revenue = revenue,
        runtime = runtime,
        budget = budget,
        episodes = null,

        themeColor = null,
        mediaSource = MediaSource.TMDB
    )
}

fun TMDbTvDetailResponse.toMediaEntity(
    converters: Converters
): MediaEntity {
    return MediaEntity(
        id = id,
        title = name ?: "Unknown",

        overview = overview ?: "",
        posterPath = posterPath,
        backdropPath = backdropPath,

        voteAverage = voteAverage?.toFloat(),
        voteCount = voteCount,
        popularity = popularity?.toFloat(),

        releaseDate = firstAirDate?.toEpochMillis(),
        finishDate = lastAirDate?.toEpochMillis(),

        mediaType = AppMediaType.TV,

        originalLanguage = originalLanguage,
        originalTitle = originalName,

        adult = adult,
        status = status?.let { converters.fromStatusString(it) }, // TMDB tv status different mapping if needed

        homepage = homepage,
        imdbId = null,
        tagline = tagline,
        revenue = null,
        runtime = episodeRunTime?.firstOrNull(),
        budget = null,
        episodes = numberOfEpisodes,

        themeColor = null,
        mediaSource = MediaSource.TMDB
    )
}

