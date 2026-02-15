package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProductionCountries",
    indices = [Index(value = ["production_countries"])]
)
data class ProductionCountryEntity(
    @PrimaryKey @ColumnInfo(name = "production_country_id") val productionCountryId: Int,
    @ColumnInfo(name = "production_countries") val productionCountries: String
)