package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.ProductionCountryEntity

@Entity(
    tableName = "MediaProductionCountries",
    primaryKeys = ["media_id", "production_country_id"],
    indices = [Index(value = ["media_id", "production_country_id"])],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id"],
            childColumns = ["media_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductionCountryEntity::class,
            parentColumns = ["production_country_id"],
            childColumns = ["production_country_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaProductionCountryCrossRef(
    @ColumnInfo(name = "media_id") val mediaId: Int,
    @ColumnInfo(name = "production_country_id") val productionCountryId: Int
)