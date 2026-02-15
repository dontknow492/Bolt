package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.SpokenLanguageEntity

@Entity(
    tableName = "MediaSpokenLanguages",
    primaryKeys = ["media_id", "spoken_language_id"],
    indices = [Index(value = ["media_id", "spoken_language_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
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
    @ColumnInfo(name = "media_id") val mediaId: Int,
    @ColumnInfo(name = "spoken_language_id") val spokenLanguageId: Int
)


