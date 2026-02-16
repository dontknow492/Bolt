package com.ghost.bolt.ui.components.card

sealed class MediaCardStyle {
    object List : MediaCardStyle()
    object Detailed : MediaCardStyle() // We will stub this for later
    data class Cover(val variant: CoverVariant = CoverVariant.NORMAL) : MediaCardStyle()
}

enum class CoverVariant {
    COMPACT, // E.g., Just the poster image
    MINIMAL, // E.g., Poster + 1 line title
    NORMAL;   // E.g., Poster + Title + Rating badge
}