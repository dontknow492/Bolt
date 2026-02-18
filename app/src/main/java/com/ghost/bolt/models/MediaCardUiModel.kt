package com.ghost.bolt.models

import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import kotlinx.serialization.Serializable


@Serializable
data class MediaCardUiModel(
    val id: Int,
    val title: String,
    val mediaType: AppMediaType,
    val mediaSource: MediaSource,

    val posterUrl: String?,
    val backdropUrl: String?,

    val voteAverage: Float?,
    val voteCount: Int?,

    val overview: String?,
    val releaseDate: String?
)

