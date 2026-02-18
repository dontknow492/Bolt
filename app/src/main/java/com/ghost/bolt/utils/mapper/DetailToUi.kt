package com.ghost.bolt.utils.mapper

import com.ghost.bolt.database.entity.MediaDetail
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSimilarCrossRef
import com.ghost.bolt.models.UiCastMember
import com.ghost.bolt.models.UiMediaDetail
import com.ghost.bolt.models.UiRelatedMedia

/**
 * Merges the Room @Relation object with raw CrossRef lists to build a UI-ready model.
 */
fun MediaDetail.toUiMediaDetail(
    castLinks: List<MediaCastCrossRef>,
    recLinks: List<MediaRecommendationCrossRef>,
    similarLinks: List<MediaSimilarCrossRef>
): UiMediaDetail {

    // 1. Merge Cast Entities with Metadata (Character Name, Order)
    val uiCast = this.cast.mapNotNull { actor ->
        val link = castLinks.find { it.castId == actor.castId }
        link?.let {
            UiCastMember(
                id = actor.castId,
                name = actor.name,
                profilePath = actor.profilePath,
                characterName = it.characterName,
                creditOrder = it.creditOrder ?: 999,
                department = it.characterName // Or add department to DB if needed
            )
        }
    }.sortedBy { it.creditOrder }

    // 2. Merge Recommendations with Metadata (Display Order)
    val uiRecommendations = this.recommendations.mapNotNull { movie ->
        val link = recLinks.find { it.targetMediaId == movie.id }
        link?.let {
            UiRelatedMedia(
                media = movie,
                displayOrder = it.displayOrder
            )
        }
    }.sortedBy { it.displayOrder }

    // 3. Merge Similar with Metadata (Display Order)
    val uiSimilar = this.similar.mapNotNull { movie ->
        val link = similarLinks.find { it.targetMediaId == movie.id }
        link?.let {
            UiRelatedMedia(
                media = movie,
                displayOrder = it.displayOrder
            )
        }
    }.sortedBy { it.displayOrder }

    return UiMediaDetail(
        media = this.media,
        genres = this.genres,
        keywords = this.keywords,
        cast = uiCast,
        crew = emptyList(), // Populate later if you add Crew tables
        recommendations = uiRecommendations,
        similar = uiSimilar,
        productionCompanies = this.productionCompanies.map { it.productionCompanies },
//        productionCountries = this.productionCountries.map { it.productionCountries },
        spokenLanguages = this.spokenLanguages.map { it.spokenLanguages }
    )
}