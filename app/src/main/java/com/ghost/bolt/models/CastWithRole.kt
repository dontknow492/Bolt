package com.ghost.bolt.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.ghost.bolt.database.entity.CastEntity

/**
 * POJO to fetch Cast details along with their role (Cross-ref data).
 * Used directly in Room queries.
 */
data class CastWithRole(
    @Embedded
    val cast: CastEntity,

    @ColumnInfo(name = "character_name")
    val characterName: String?,

    @ColumnInfo(name = "credit_order")
    val creditOrder: Int?
)

