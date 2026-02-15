package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Cast",
    indices = [Index(value = ["name"])]
)
data class CastEntity(
    @PrimaryKey @ColumnInfo(name = "cast_id") val castId: Int,
    val name: String,
    @ColumnInfo(name = "profile_path") val profilePath: String?,
    @ColumnInfo(name = "known_for_department") val knownForDepartment: String?
)