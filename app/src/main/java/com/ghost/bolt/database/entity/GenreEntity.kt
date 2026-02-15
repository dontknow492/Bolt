package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Genre",
    indices = [Index(value = ["name"])]
)
data class GenreEntity(
    @PrimaryKey @ColumnInfo(name = "genre_id") val genreId: Int,
    val name: String
)

