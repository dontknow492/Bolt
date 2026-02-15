package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ghost.bolt.enums.RefreshFrequency

@Entity(
    tableName = "Categories",
    indices = [Index(value = ["name"])]
)
data class CategoryEntity(
    @PrimaryKey @ColumnInfo(name = "category_id") val categoryId: Int,
    val name: String,
    val explanation: String?,
    @ColumnInfo(name = "refresh_frequency") val refreshFrequency: RefreshFrequency?
)