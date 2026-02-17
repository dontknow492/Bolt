package com.ghost.bolt.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.GenreEntity
import com.ghost.bolt.database.entity.KeywordEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.ProductionCompanyEntity
import com.ghost.bolt.database.entity.ProductionCountryEntity
import com.ghost.bolt.database.entity.SpokenLanguageEntity
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaGenreCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaKeywordCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaProductionCompanyCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaProductionCountryCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSimilarCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSpokenLanguageCrossRef
import com.ghost.bolt.models.MediaDecomposition

@Dao
interface MediaDecompositionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenreCrossRefs(refs: List<MediaGenreCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKeywords(keywords: List<KeywordEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertKeywordCrossRefs(refs: List<MediaKeywordCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompanies(companies: List<ProductionCompanyEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompanyCrossRefs(refs: List<MediaProductionCompanyCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCast(cast: List<CastEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCastCrossRefs(refs: List<MediaCastCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLanguages(languages: List<SpokenLanguageEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLanguageCrossRefs(refs: List<MediaSpokenLanguageCrossRef>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountries(countries: List<ProductionCountryEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCountryCrossRefs(refs: List<MediaProductionCountryCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecommended(media: List<MediaEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecommendationCrossRefs(refs: List<MediaRecommendationCrossRef>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimilar(media: List<MediaEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSimilarCrossRefs(refs: List<MediaSimilarCrossRef>)


    @Transaction
    suspend fun insertDecomposition(decomposition: MediaDecomposition) {

        insertMedia(decomposition.media)

        insertGenres(decomposition.genres)
        insertGenreCrossRefs(decomposition.genreCrossRefs)

        insertKeywords(decomposition.keywords)
        insertKeywordCrossRefs(decomposition.keywordsCrossRefs)

        insertCompanies(decomposition.companies)
        insertCompanyCrossRefs(decomposition.companyCrossRefs)

        insertCast(decomposition.cast)
        insertCastCrossRefs(decomposition.castCrossRefs)

        insertLanguages(decomposition.spokenLanguage)
        insertLanguageCrossRefs(decomposition.spokenLanguageCrossRefs)

        insertCountries(decomposition.productionCountries)
        insertCountryCrossRefs(decomposition.productionCountriesCrossRefs)

        insertRecommended(decomposition.recommendations)
        insertRecommendationCrossRefs(decomposition.recommendationCrossRefs)

        insertSimilar(decomposition.similar)
        insertSimilarCrossRefs(decomposition.similarCrossRefs)
    }


}
