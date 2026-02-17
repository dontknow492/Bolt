package com.ghost.bolt.models

import com.ghost.bolt.database.entity.MediaEntity

/**
 * Represents a Recommended/Similar Movie for the UI, preserving the API's relevance order.
 */
data class UiRelatedMedia(
    val media: MediaEntity,
    val displayOrder: Int
)