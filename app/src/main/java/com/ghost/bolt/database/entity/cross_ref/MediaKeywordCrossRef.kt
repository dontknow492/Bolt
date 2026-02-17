package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.KeywordEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaKeywords",
    primaryKeys = ["media_id", "media_type", "media_source", "keyword_id"],
    indices = [Index(value = ["media_id", "keyword_id"]), Index("keyword_id")],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["media_id", "media_type", "media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = KeywordEntity::class,
            parentColumns = ["keyword_id"],
            childColumns = ["keyword_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaKeywordCrossRef(
    @ColumnInfo(name = "media_id")
    val mediaId: Int,

    @ColumnInfo(name = "media_type")
    val mediaType: AppMediaType,

    @ColumnInfo(name = "media_source")
    val mediaSource: MediaSource,
    @ColumnInfo(name = "keyword_id") val keywordId: Int
)

