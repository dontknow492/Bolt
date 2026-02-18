package com.ghost.bolt.enums

import com.ghost.bolt.enums.tdmb.TMDbMediaCategory

enum class AppCategory(
    val id: Int,
    val label: String,
    val title: String,
) {

    POPULAR(
        id = 0,
        label = "Popular",
        title = "Popular"
    ),

    TRENDING(
        id = 1,
        label = "Trending",
        title = "Trending"
    ),

    TOP_RATED(
        id = 2,
        label = "Top Rated",
        title = "Top Rated"
    ),

    UPCOMING(
        id = 3,
        label = "Upcoming",
        title = "Coming Soon"
    ),

    NOW_PLAYING(
        id = 4,
        label = "Now Playing",
        title = "On Theaters"
    ),

    SEASONAL(
        id = 5,
        label = "Seasonal",
        title = "Seasonal"
    );


    companion object {
        fun fromId(id: Int): AppCategory {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("Invalid AppCategory id: $id")
        }
    }
}


fun AppCategory.toTmdbCategoryOrNull(): TMDbMediaCategory? {
    return when (this) {
        AppCategory.POPULAR -> TMDbMediaCategory.POPULAR
        AppCategory.TOP_RATED -> TMDbMediaCategory.TOP_RATED
        AppCategory.UPCOMING -> TMDbMediaCategory.UPCOMING
        AppCategory.NOW_PLAYING -> TMDbMediaCategory.NOW_PLAYING

        // Not part of standard list endpoint
        AppCategory.TRENDING -> TMDbMediaCategory.TRENDING

        // Not supported by TMDB
        AppCategory.SEASONAL -> null
    }
}