package com.ghost.bolt.enums.tdmb

/**
 * Type-safe Enums for API parameters
 */
enum class TmdbMediaType(val value: String) {
    MOVIE("movie"),
    TV("tv");

    override fun toString() = value
}