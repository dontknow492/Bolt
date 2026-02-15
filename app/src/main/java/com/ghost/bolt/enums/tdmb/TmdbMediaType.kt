package com.ghost.bolt.enums.tdmb

/**
 * Type-safe Enums for API parameters
 */
enum class TmdbMediaType(val value: String) {
    ALL("all"),
    MOVIE("movie"),
    TV("tv"),
    PERSON("person");

    override fun toString() = value
}