package com.ghost.bolt.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaGenreCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaKeywordCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaProductionCompanyCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSimilarCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSpokenLanguageCrossRef

data class MediaDetail(
    @Embedded val media: MediaEntity,

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
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MediaRecommendationCrossRef::class,
            parentColumn = "source_media_id",
            entityColumn = "target_media_id"
        )
    )
    val recommendations: List<MediaEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MediaSimilarCrossRef::class,
            parentColumn = "source_media_id",
            entityColumn = "target_media_id"
        )
    )
    val similar: List<MediaEntity>
)