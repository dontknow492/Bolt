package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.CategoryEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaCategories",
    primaryKeys = ["media_id", "media_type", "media_source", "category_id"],
    indices = [Index(value = ["media_id", "category_id"]), Index("category_id")],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["media_id", "media_type", "media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaCategoryCrossRef(
    @ColumnInfo(name = "media_id")
    val mediaId: Int,

    @ColumnInfo(name = "media_type")
    val mediaType: AppMediaType,

    @ColumnInfo(name = "media_source")
    val mediaSource: MediaSource,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "position_in_category") val positionInCategory: Int? // Crucial for Paging order
)

// Repeat this CrossRef pattern for MediaKeywords, MediaProductionCompanies, etc.
