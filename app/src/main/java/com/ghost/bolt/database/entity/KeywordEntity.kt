package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Keywords",
    indices = [Index(value = ["keywords"])]
)
data class KeywordEntity(
    @PrimaryKey @ColumnInfo(name = "keyword_id") val keywordId: Int,
    val keywords: String
)