package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.CategoryEntity
import com.ghost.bolt.database.entity.MediaEntity

@Entity(
    tableName = "MediaCategories",
    primaryKeys = ["media_id", "category_id"],
    indices = [Index(value = ["media_id", "category_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
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
    @ColumnInfo(name = "media_id") val mediaId: Int,
    @ColumnInfo(name = "category_id") val categoryId: Int,
    @ColumnInfo(name = "position_in_category") val positionInCategory: Int? // Crucial for Paging order
)

// Repeat this CrossRef pattern for MediaKeywords, MediaProductionCompanies, etc.
