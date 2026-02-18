package com.ghost.bolt.enums

/**
 * Type-safe Enum for Append To Response options
 */
enum class AppendToResponseItem(val value: String) {

    // Common
    CREDITS("credits"),
    KEYWORDS("keywords"),
    SIMILAR("similar"),
    VIDEOS("videos"),
    IMAGES("images"),
    REVIEWS("reviews"),
    RECOMMENDATIONS("recommendations"),

    // Very Useful Extras
    EXTERNAL_IDS("external_ids"),
    WATCH_PROVIDERS("watch/providers"),
    RELEASE_DATES("release_dates"),      // movie only
    CONTENT_RATINGS("content_ratings"),  // tv only

    // Optional / Advanced
    ALTERNATIVE_TITLES("alternative_titles"),
    TRANSLATIONS("translations"),
    LISTS("lists"),
    CHANGES("changes"),
    ACCOUNT_STATES("account_states")
}
