package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.MediaEntity

@Entity(
    tableName = "MediaCast",
    primaryKeys = ["media_id", "cast_id"],
    indices = [Index(value = ["media_id", "cast_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
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
    @ColumnInfo(name = "media_id") val mediaId: Int,
    @ColumnInfo(name = "cast_id") val castId: Int,
    @ColumnInfo(name = "character_name") val characterName: String?,
    @ColumnInfo(name = "credit_order") val creditOrder: Int? // Used to sort lead actors first
)


