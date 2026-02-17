package com.ghost.bolt.database.entity.cross_ref

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.ProductionCompanyEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource

@Entity(
    tableName = "MediaProductionCompanies",
    primaryKeys = ["media_id", "media_type", "media_source", "production_company_id"],
    indices = [Index(value = ["media_id", "production_company_id"]), Index("production_company_id")],
    foreignKeys = [
        ForeignKey(
            entity = MediaEntity::class,
            parentColumns = ["id", "media_type", "media_source"],
            childColumns = ["media_id", "media_type", "media_source"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductionCompanyEntity::class,
            parentColumns = ["production_company_id"],
            childColumns = ["production_company_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MediaProductionCompanyCrossRef(
    @ColumnInfo(name = "media_id")
    val mediaId: Int,

    @ColumnInfo(name = "media_type")
    val mediaType: AppMediaType,

    @ColumnInfo(name = "media_source")
    val mediaSource: MediaSource,
    @ColumnInfo(name = "production_company_id") val productionCompanyId: Int
)