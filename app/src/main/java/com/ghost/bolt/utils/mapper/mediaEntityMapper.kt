package com.ghost.bolt.utils.mapper

import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.ui.screen.formatTimestampToDate
import com.ghost.bolt.utils.TmdbConfig

fun MediaEntity.toMediaCardUiModel(
    posterSize: TmdbConfig.TMDbPosterSize = TmdbConfig.TMDbPosterSize.W342,
    backdropSize: TmdbConfig.TmdbBackdropSize = TmdbConfig.TmdbBackdropSize.W780
): MediaCardUiModel {
    val posterUrl = TmdbConfig.getPosterUrl(this.posterPath, posterSize)
    val backdropUrl = TmdbConfig.getBackdropUrl(this.backdropPath, backdropSize)
    return MediaCardUiModel(
        id = id,
        title = title,
        mediaType = mediaType,
        mediaSource = mediaSource,
        posterUrl = posterUrl,
        backdropUrl = backdropUrl,
        voteAverage = voteAverage,
        voteCount = voteCount,
        overview = overview,
        releaseDate = formatTimestampToDate(releaseDate),
    )
}