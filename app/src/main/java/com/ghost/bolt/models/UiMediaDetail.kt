package com.ghost.bolt.models

import com.ghost.bolt.database.entity.GenreEntity
import com.ghost.bolt.database.entity.MediaEntity

/**
 * The master object for your Detail Screen.
 * This decouples the UI from the complex database relationships.
 */
data class UiMediaDetail(
    // Core Media Data
    val media: MediaEntity,

    // Metadata Lists (Simplified for UI consumption)
    val genres: List<GenreEntity>,
    val keywords: List<String>,

    // Complex Relationships with Metadata
    val cast: List<UiCastMember>,
    val crew: List<UiCastMember> = emptyList(), // Future-proofing for Director/Writer

    // Related Content
    val recommendations: List<UiRelatedMedia>,
    val similar: List<UiRelatedMedia>,


    // Production Info
    val productionCompanies: List<String> = emptyList(),
    val productionCountries: List<String> = emptyList(),
    val spokenLanguages: List<String> = emptyList()
)