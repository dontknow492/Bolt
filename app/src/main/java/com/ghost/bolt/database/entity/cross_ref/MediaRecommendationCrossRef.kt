package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.MediaEntity

@Entity(
    tableName = "MediaRecommendations",
    primaryKeys = ["source_media_id", "target_media_id"],
    indices = [
        Index(value = ["source_media_id"]),
        Index(value = ["target_media_id"])
    ],
    foreignKeys = [
        // If the SOURCE movie is deleted, delete its recommendations
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_media_id"],
            onDelete = ForeignKey.CASCADE
        ),
        // If the TARGET movie is deleted, remove it from recommendation lists
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["target_media_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaRecommendationCrossRef(
    @ColumnInfo(name = "source_media_id") val sourceMediaId: Int,
    @ColumnInfo(name = "target_media_id") val targetMediaId: Int,
    @ColumnInfo(name = "display_order") val displayOrder: Int
)