package com.ghost.bolt.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ProductionCompanies",
    indices = [Index(value = ["production_companies"])]
)
data class ProductionCompanyEntity(
    @PrimaryKey @ColumnInfo(name = "production_company_id") val productionCompanyId: Int,
    @ColumnInfo(name = "production_companies") val productionCompanies: String
)