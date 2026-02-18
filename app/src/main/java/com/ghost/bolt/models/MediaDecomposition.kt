package com.ghost.bolt.models


import com.ghost.bolt.api.tmdb.response.TMDbMovieDetailResponse
import com.ghost.bolt.api.tmdb.response.TMDbTvDetailResponse
import com.ghost.bolt.database.Converters
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

fun TMDbMovieDetailResponse.toDecomposition(
    converters: Converters
): MediaDecomposition {

    val mediaEntity = this.toMediaEntity(converters)
    val mediaId = mediaEntity.id
    val mediaType = mediaEntity.mediaType
    val mediaSource = mediaEntity.mediaSource

    // 1️⃣ Genres
    val genres = genres?.map {
        GenreEntity(it.id, it.name)
    } ?: emptyList()

    val genreCrossRefs = genres.map {
        MediaGenreCrossRef(mediaId, mediaType, mediaSource, it.genreId)
    }

    // 2️⃣ Keywords
    val keywordEntities = keywords?.all?.map {
        KeywordEntity(it.id, it.name)
    } ?: emptyList()

    val keywordCrossRefs = keywordEntities.map {
        MediaKeywordCrossRef(mediaId, mediaType, mediaSource, it.keywordId)
    }

    // 3️⃣ Companies
    val companies = productionCompanies?.map {
        ProductionCompanyEntity(it.id, it.name)
    } ?: emptyList()

    val companyCrossRefs = companies.map {
        MediaProductionCompanyCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            it.productionCompanyId
        )
    }

    // 4️⃣ Cast
    val castEntities = credits?.cast?.map {
        CastEntity(
            castId = it.id,
            name = it.name ?: "Unknown",
            profilePath = it.profilePath,
            knownForDepartment = it.knownForDepartment
        )
    } ?: emptyList()

    val castCrossRefs = credits?.cast?.map {
        MediaCastCrossRef(
            mediaId = mediaId,
            mediaType = mediaType,
            mediaSource = mediaSource,
            castId = it.id,
            characterName = it.character,
            creditOrder = it.order ?: 0
        )
    } ?: emptyList()

    // 5️⃣ Spoken Languages
    val languages = spokenLanguages?.map {
        SpokenLanguageEntity(
            spokenLanguageId = it.isoCode.hashCode(),
            spokenLanguages = it.name
        )
    } ?: emptyList()

    val languageCrossRefs = languages.map {
        MediaSpokenLanguageCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            it.spokenLanguageId
        )
    }

    // 6️⃣ Production Countries
    val countries = productionCountries?.map {
        ProductionCountryEntity(
            productionCountryId = it.isoCode.hashCode(),
            productionCountries = it.name
        )
    } ?: emptyList()

    val countryCrossRefs = countries.map {
        MediaProductionCountryCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            it.productionCountryId
        )
    }

    // 7️⃣ Recommendations
    val recommendationsList =
        recommendations?.results?.map { it.toMediaEntity() } ?: emptyList()

    val recommendationCrossRefs = recommendationsList.mapIndexed { index, target ->
        MediaRecommendationCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            target.id,
            target.mediaType,
            target.mediaSource,
            displayOrder = index
        )
    }

    // 8️⃣ Similar
    val similarList =
        similar?.results?.map { it.toMediaEntity() } ?: emptyList()

    val similarCrossRefs = similarList.mapIndexed { index, target ->
        MediaSimilarCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            target.id,
            target.mediaType,
            target.mediaSource,
            displayOrder = index
        )
    }

    return MediaDecomposition(
        media = mediaEntity,
        genres = genres,
        genreCrossRefs = genreCrossRefs,
        companies = companies,
        companyCrossRefs = companyCrossRefs,
        recommendations = recommendationsList,
        recommendationCrossRefs = recommendationCrossRefs,
        keywords = keywordEntities,
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


fun TMDbTvDetailResponse.toDecomposition(
    converters: Converters
): MediaDecomposition {

    val mediaEntity = this.toMediaEntity(converters)
    val mediaId = mediaEntity.id
    val mediaType = mediaEntity.mediaType
    val mediaSource = mediaEntity.mediaSource

    // Genres
    val genres = genres?.map {
        GenreEntity(it.id, it.name)
    } ?: emptyList()

    val genreCrossRefs = genres.map {
        MediaGenreCrossRef(mediaId, mediaType, mediaSource, it.genreId)
    }

    // Companies (TV networks optional)
    val companies = productionCompanies?.map {
        ProductionCompanyEntity(it.id, it.name)
    } ?: emptyList()

    val companyCrossRefs = companies.map {
        MediaProductionCompanyCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            it.productionCompanyId
        )
    }

    // Cast
    val castEntities = credits?.cast?.map {
        CastEntity(
            castId = it.id,
            name = it.name ?: "Unknown",
            profilePath = it.profilePath,
            knownForDepartment = it.knownForDepartment
        )
    } ?: emptyList()

    val castCrossRefs = credits?.cast?.map {
        MediaCastCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            it.id,
            it.character,
            it.order ?: 0
        )
    } ?: emptyList()

    // Recommendations
    val recommendationsList =
        recommendations?.results?.map { it.toMediaEntity() } ?: emptyList()

    val recommendationCrossRefs = recommendationsList.mapIndexed { index, target ->
        MediaRecommendationCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            target.id,
            target.mediaType,
            target.mediaSource,
            displayOrder = index
        )
    }

    // Similar
    val similarList =
        similar?.results?.map { it.toMediaEntity() } ?: emptyList()

    val similarCrossRefs = similarList.mapIndexed { index, target ->
        MediaSimilarCrossRef(
            mediaId,
            mediaType,
            mediaSource,
            target.id,
            target.mediaType,
            target.mediaSource,
            displayOrder = index
        )
    }

    return MediaDecomposition(
        media = mediaEntity,
        genres = genres,
        genreCrossRefs = genreCrossRefs,
        companies = companies,
        companyCrossRefs = companyCrossRefs,
        recommendations = recommendationsList,
        recommendationCrossRefs = recommendationCrossRefs,
        keywords = emptyList(), // TV keywords differ if needed
        keywordsCrossRefs = emptyList(),
        cast = castEntities,
        castCrossRefs = castCrossRefs,
        spokenLanguage = emptyList(),
        spokenLanguageCrossRefs = emptyList(),
        productionCountries = emptyList(),
        productionCountriesCrossRefs = emptyList(),
        similar = similarList,
        similarCrossRefs = similarCrossRefs
    )
}




