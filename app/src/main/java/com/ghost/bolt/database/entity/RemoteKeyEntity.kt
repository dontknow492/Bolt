package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "RemoteKeys")
data class RemoteKeyEntity(
    @PrimaryKey @ColumnInfo(name = "label_id") val labelId: String,
    @ColumnInfo(name = "next_page") val nextPage: Int?,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long
)