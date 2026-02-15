package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.Status

@Entity(
    tableName = "Media",
    indices = [Index(value = ["title"])]
)
data class MediaEntity(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(name = "vote_average") val voteAverage: Float?,
    @ColumnInfo(name = "vote_count") val voteCount: Int?,
    @ColumnInfo(name = "status") val status: Status?,
    @ColumnInfo(name = "release_date") val releaseDate: Long?, // Stored as Unix Timestamp
    @ColumnInfo(name = "finish_date") val finishDate: Long?,
    val revenue: Long?,
    val runtime: Int?,
    val adult: Boolean?,
    @ColumnInfo(name = "backdrop_path") val backdropPath: String?,
    val budget: Long?,
    val homepage: String?,
    @ColumnInfo(name = "imdb_id") val imdbId: String?,
    @ColumnInfo(name = "original_language") val originalLanguage: String?,
    @ColumnInfo(name = "original_title") val originalTitle: String?,
    val overview: String?,
    val popularity: Float?,
    @ColumnInfo(name = "poster_path") val posterPath: String?,
    val tagline: String?,
    @ColumnInfo(name = "media_type") val mediaType: AppMediaType?,
    @ColumnInfo(name = "episodes") val episodes: Int?
)

