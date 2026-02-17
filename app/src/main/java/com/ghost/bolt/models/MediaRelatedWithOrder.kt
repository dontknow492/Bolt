package com.ghost.bolt.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ghost.bolt.database.entity.MediaEntity

/**
 * POJO to fetch Related Media (Recommendations/Similar) with their display order.
 */
data class MediaRelatedWithOrder(
    @Embedded
    val media: MediaEntity,

    @ColumnInfo(name = "display_order")
    val displayOrder: Int?
)