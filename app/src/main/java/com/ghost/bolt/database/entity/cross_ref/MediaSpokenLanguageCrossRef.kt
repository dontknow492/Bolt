package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.SpokenLanguageEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaSpokenLanguages",
    primaryKeys = ["media_id", "media_type", "media_source", "spoken_language_id"],
    indices = [Index(value = ["media_id", "spoken_language_id"]), Index("spoken_language_id")],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["media_id", "media_type", "media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SpokenLanguageEntity::class,
            parentColumns = ["spoken_language_id"],
            childColumns = ["spoken_language_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaSpokenLanguageCrossRef(
    @ColumnInfo(name = "media_id")
    val mediaId: Int,

    @ColumnInfo(name = "media_type")
    val mediaType: AppMediaType,

    @ColumnInfo(name = "media_source")
    val mediaSource: MediaSource,
    @ColumnInfo(name = "spoken_language_id") val spokenLanguageId: Int
)
