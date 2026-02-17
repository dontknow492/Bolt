package com.ghost.bolt.models

import com.ghost.bolt.api.response.TMDbNetworkMedia
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
import com.ghost.bolt.utils.mapper.toMediaEntity

/**
 * Data class to hold the results of decomposing a NetworkMovie
 * so they can be inserted into the database.
 */
data class MediaDecomposition(
    val media: MediaEntity,
    val genres: List<GenreEntity>,
    val genreCrossRefs: List<MediaGenreCrossRef>,
    val companies: List<ProductionCompanyEntity>,
    val companyCrossRefs: List<MediaProductionCompanyCrossRef>,
    val recommendationCrossRefs: List<MediaRecommendationCrossRef>,
    val recommendations: List<MediaEntity>,
    val keywords: List<KeywordEntity>,
    val keywordsCrossRefs: List<MediaKeywordCrossRef>,
    val cast: List<CastEntity>,
    val castCrossRefs: List<MediaCastCrossRef>,
    val spokenLanguage: List<SpokenLanguageEntity>,
    val spokenLanguageCrossRefs: List<MediaSpokenLanguageCrossRef>, // Corrected type
    val productionCountries: List<ProductionCountryEntity>,
    val productionCountriesCrossRefs: List<MediaProductionCountryCrossRef>, // Corrected type
    val similarCrossRefs: List<MediaSimilarCrossRef>,
    val similar: List<MediaEntity>
)

fun TMDbNetworkMedia.toDecomposition(): MediaDecomposition {
    val mediaId = this.id
    val mediaEntity = this.toMediaEntity()
    val mediaType = mediaEntity.mediaType
    val mediaSource = mediaEntity.mediaSource


    // 1. Genres
    val genres = this.genres?.map { GenreEntity(genreId = it.id, name = it.name) } ?: emptyList()
    val genreCrossRefs =
        genres.map { MediaGenreCrossRef(mediaId, mediaType, mediaSource, it.genreId) }

    // 2. Keywords
    val keywords = this.keywords?.all?.map { KeywordEntity(keywordId = it.id, keywords = it.name) }
        ?: emptyList()
    val keywordCrossRefs =
        keywords.map { MediaKeywordCrossRef(mediaId, mediaType, mediaSource, it.keywordId) }

    // 3. Companies
    val companies = this.productionCompanies?.map {
        ProductionCompanyEntity(productionCompanyId = it.id, productionCompanies = it.name)
    } ?: emptyList()
    val companyCrossRefs =
        companies.map {
            MediaProductionCompanyCrossRef(
                mediaId,
                mediaType,
                mediaSource,
                it.productionCompanyId
            )
        }

    // 4. Cast (Note: We use the hash of the ISO code if your Entity ID is Int)
    val castEntities = this.credits?.cast?.map { networkCast ->
        CastEntity(
            castId = networkCast.id,
            name = networkCast.name ?: "Unknown",
            profilePath = networkCast.profilePath,
            knownForDepartment = networkCast.knownForDepartment
        )
    } ?: emptyList()

// Create the relationship links containing movie-specific data
    val castCrossRefs = this.credits?.cast?.map { networkCast ->
        MediaCastCrossRef(
            mediaId = mediaId,
            mediaType = mediaType,
            mediaSource = mediaSource,
            castId = networkCast.id,
            characterName = networkCast.character, // From NetworkCast
            creditOrder = networkCast.order ?: 0 // From NetworkCast
        )
    } ?: emptyList()

    // 5. Spoken Languages
    val languages = this.spokenLanguages?.map {
        SpokenLanguageEntity(spokenLanguageId = it.isoCode.hashCode(), spokenLanguages = it.name)
    } ?: emptyList()
    val languageCrossRefs =
        languages.map {
            MediaSpokenLanguageCrossRef(
                mediaId,
                mediaType,
                mediaSource,
                it.spokenLanguageId
            )
        }

    // 6. Production Countries
    val countries = this.productionCountries?.map {
        ProductionCountryEntity(
            productionCountryId = it.isoCode.hashCode(),
            productionCountries = it.name
        )
    } ?: emptyList()
    val countryCrossRefs =
        countries.map {
            MediaProductionCountryCrossRef(
                mediaId,
                mediaType,
                mediaSource,
                it.productionCountryId
            )
        }

    // 7. Recommendations
    val recommendedList = this.recommendations?.results?.map { it.toMediaEntity() } ?: emptyList()
    val recommendationCrossRefs = recommendedList.mapIndexed { index, target ->
        MediaRecommendationCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            target.id,
            targetMediaType = target.mediaType,
            targetMediaSource = target.mediaSource,
            displayOrder = index
        )
    }

    // 8. Similar
    val similarList = this.similar?.results?.map { it.toMediaEntity() } ?: emptyList()
    val similarCrossRefs = similarList.mapIndexed { index, target ->
        MediaSimilarCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            target.id,
            targetMediaType = target.mediaType,
            targetMediaSource = target.mediaSource,
            displayOrder = index
        )
    }

    return MediaDecomposition(
        media = mediaEntity,
        genres = genres,
        genreCrossRefs = genreCrossRefs,
        companies = companies,
        companyCrossRefs = companyCrossRefs,
        recommendations = recommendedList,
        recommendationCrossRefs = recommendationCrossRefs,
        keywords = keywords,
        keywordsCrossRefs = keywordCrossRefs,
        cast = castEntities,
        castCrossRefs = castCrossRefs,
        spokenLanguage = languages,
        spokenLanguageCrossRefs = languageCrossRefs,
        productionCountries = countries,
        productionCountriesCrossRefs = countryCrossRefs,
        similar = similarList,
        similarCrossRefs = similarCrossRefs
    )
}


