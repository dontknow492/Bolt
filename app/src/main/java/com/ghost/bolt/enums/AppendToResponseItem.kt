package com.ghost.bolt.enums

/**
 * Type-safe Enum for Append To Response options
 */
enum class AppendToResponseItem(val value: String) {
    CREDITS("credits"),
    SIMILAR("similar"),
    VIDEOS("videos"),
    IMAGES("images"),
    REVIEWS("reviews"),
    RECOMMENDATIONS("recommendations");
}