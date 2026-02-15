package com.ghost.bolt.enums.tdmb


enum class TMDbMediaCategory(
    val id: Int,                 // Stable DB ID
    val path: String,            // TMDB API path
    val supportedTypes: Set<TmdbMediaType>
) {

    POPULAR(
        id = 0,
        path = "popular",
        supportedTypes = setOf(TmdbMediaType.MOVIE, TmdbMediaType.TV)
    ),

    TOP_RATED(
        id = 1,
        path = "top_rated",
        supportedTypes = setOf(TmdbMediaType.MOVIE, TmdbMediaType.TV)
    ),

    UPCOMING(
        id = 2,
        path = "upcoming",
        supportedTypes = setOf(TmdbMediaType.MOVIE)
    ),

    NOW_PLAYING(
        id = 3,
        path = "now_playing",
        supportedTypes = setOf(TmdbMediaType.MOVIE)
    ),

    AIRING_TODAY(
        id = 4,
        path = "airing_today",
        supportedTypes = setOf(TmdbMediaType.TV)
    ),

    ON_THE_AIR(
        id = 5,
        path = "on_the_air",
        supportedTypes = setOf(TmdbMediaType.TV)
    ),
    TRENDING(
        id = 6,
        path = "trending",
        supportedTypes = setOf(TmdbMediaType.MOVIE, TmdbMediaType.TV)
    );

    companion object {
        fun fromId(id: Int): TMDbMediaCategory {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Invalid TDMBMediaCategory id: $id")
        }
    }
}