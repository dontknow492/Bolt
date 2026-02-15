package com.ghost.bolt.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaGenreCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaKeywordCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaProductionCompanyCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSpokenLanguageCrossRef

//import com.ghost.bolt.database.entity.*

data class MediaDetail(
    // 1. The Core Media Data
    @Embedded val media: MediaEntity,

    // 2. The Many-to-Many Relations
    // Room will look at the 'MediaGenre' table to find matches
    @Relation(
        parentColumn = "id",
        entityColumn = "genre_id",
        associateBy = Junction(
            value = MediaGenreCrossRef::class,
            parentColumn = "media_id",
            entityColumn = "genre_id"
        )
    )
    val genres: List<GenreEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "keyword_id",
        associateBy = Junction(
            value = MediaKeywordCrossRef::class,
            parentColumn = "media_id",
            entityColumn = "keyword_id"
        )
    )
    val keywords: List<KeywordEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "production_company_id",
        associateBy = Junction(
            value = MediaProductionCompanyCrossRef::class,
            parentColumn = "media_id",
            entityColumn = "production_company_id"
        )
    )
    val productionCompanies: List<ProductionCompanyEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "spoken_language_id",
        associateBy = Junction(
            value = MediaSpokenLanguageCrossRef::class,
            parentColumn = "media_id",
            entityColumn = "spoken_language_id"
        )
    )
    val spokenLanguages: List<SpokenLanguageEntity>,

    // 3. Cast (Sorted by credit order!)
    // Note: Room Relations don't natively support "ORDER BY" inside the relation annotation easily.
    // Usually, you fetch the CrossRef to get the order, or sort this list in your Mapper/ViewModel.
    @Relation(
        parentColumn = "id",
        entityColumn = "cast_id",
        associateBy = Junction(
            value = MediaCastCrossRef::class,
            parentColumn = "media_id",
            entityColumn = "cast_id"
        )
    )
    val cast: List<CastEntity>,

    @Relation(
        parentColumn = "id", // ID of the movie we are looking at
        entityColumn = "id", // ID of the recommended movies
        associateBy = Junction(
            value = MediaRecommendationCrossRef::class,
            parentColumn = "source_media_id",
            entityColumn = "target_media_id"
        )
    )
    val recommendations: List<MediaEntity>
)