package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.GenreEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaGenre",
    primaryKeys = ["media_id", "media_type", "media_source", "genre_id"],
    indices = [Index(value = ["media_id", "genre_id"]), Index("genre_id")],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["media_id", "media_type", "media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["genre_id"],
            childColumns = ["genre_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaGenreCrossRef(
    @ColumnInfo(name = "media_id")
    val mediaId: Int,

    @ColumnInfo(name = "media_type")
    val mediaType: AppMediaType,

    @ColumnInfo(name = "media_source")
    val mediaSource: MediaSource,
    @ColumnInfo(name = "genre_id") val genreId: Int
)

