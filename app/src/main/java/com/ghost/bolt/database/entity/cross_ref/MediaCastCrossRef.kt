package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaCast",
    primaryKeys = ["media_id", "media_type", "media_source", "cast_id"],
    indices = [Index(value = ["media_id", "cast_id"]), Index("cast_id")],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["media_id", "media_type", "media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CastEntity::class,
            parentColumns = ["cast_id"],
            childColumns = ["cast_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaCastCrossRef(
    @ColumnInfo(name = "media_id")
    val mediaId: Int,

    @ColumnInfo(name = "media_type")
    val mediaType: AppMediaType,

    @ColumnInfo(name = "media_source")
    val mediaSource: MediaSource,
    @ColumnInfo(name = "cast_id") val castId: Int,
    @ColumnInfo(name = "character_name") val characterName: String?,
    @ColumnInfo(name = "credit_order") val creditOrder: Int? // Used to sort lead actors first
)


