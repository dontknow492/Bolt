package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "SpokenLanguages",
    indices = [Index(value = ["spoken_languages"])]
)
data class SpokenLanguageEntity(
    @PrimaryKey @ColumnInfo(name = "spoken_language_id") val spokenLanguageId: Int,
    @ColumnInfo(name = "spoken_languages") val spokenLanguages: String
)