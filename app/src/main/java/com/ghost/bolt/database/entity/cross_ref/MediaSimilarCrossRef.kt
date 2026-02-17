package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaSimilar",
    primaryKeys = [
        "source_media_id", "source_media_type", "source_media_source",
        "target_media_id", "target_media_type", "target_media_source"
    ],
    indices = [
        Index(value = ["source_media_id", "source_media_type", "source_media_source"]),
        Index(value = ["target_media_id", "target_media_type", "target_media_source"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["source_media_id", "source_media_type", "source_media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["target_media_id", "target_media_type", "target_media_source"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaSimilarCrossRef(

    @ColumnInfo(name = "source_media_id")
    val sourceMediaId: Int,

    @ColumnInfo(name = "source_media_type")
    val sourceMediaType: AppMediaType,

    @ColumnInfo(name = "source_media_source")
    val sourceMediaSource: MediaSource,

    @ColumnInfo(name = "target_media_id")
    val targetMediaId: Int,

    @ColumnInfo(name = "target_media_type")
    val targetMediaType: AppMediaType,

    @ColumnInfo(name = "target_media_source")
    val targetMediaSource: MediaSource,

    @ColumnInfo(name = "display_order")
    val displayOrder: Int
)