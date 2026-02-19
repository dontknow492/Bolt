package com.ghost.bolt.enums

import kotlinx.serialization.Serializable

@Serializable
data class DiscoverFilter(

    val mediaType: AppMediaType = AppMediaType.MOVIE,

    // 🎭 Genres
    val genres: Set<Int> = emptySet(),
    val genreLogic: LogicMode = LogicMode.OR, // OR = ","  AND = "|"

    // 👤 Cast / Crew
    val cast: Set<Int> = emptySet(),
    val crew: Set<Int> = emptySet(),

    // 🏷 Keywords
    val keywords: Set<Int> = emptySet(),

    // 📅 Year Range (converted to date later)
    val minYear: Int? = null,
    val maxYear: Int? = null,

    // ⭐ Ratings
    val minVote: Float? = null,
    val maxVote: Float? = null,
    val minVoteCount: Int? = 50,

    // ⏱ Runtime (movies only)
    val minRuntime: Int? = null,
    val maxRuntime: Int? = null,

    // 📺 Networks (TV only)
    val networks: Set<Int> = emptySet(),

    // 🔞 Adult toggle
    val includeAdult: Boolean = false,

    // 🔽 Sorting
    val sortOption: DiscoverSortOption = DiscoverSortOption.POPULARITY,
    val sortOrder: SortOrder = SortOrder.DESC
)

fun DiscoverFilter.genresParam(): String? {
    if (genres.isEmpty()) return null

    return when (genreLogic) {
        LogicMode.OR -> genres.joinToString(",")
        LogicMode.AND -> genres.joinToString("|")
    }
}

fun DiscoverFilter.castParam(): String? =
    cast.takeIf { it.isNotEmpty() }?.joinToString(",")

fun DiscoverFilter.crewParam(): String? =
    crew.takeIf { it.isNotEmpty() }?.joinToString(",")

fun DiscoverFilter.keywordsParam(): String? =
    keywords.takeIf { it.isNotEmpty() }?.joinToString(",")

fun DiscoverFilter.networksParam(): String? =
    networks.takeIf { it.isNotEmpty() }?.joinToString(",")


fun DiscoverFilter.minDate(): String? =
    minYear?.let { "$it-01-01" }

fun DiscoverFilter.maxDate(): String? =
    maxYear?.let { "$it-12-31" }


enum class DiscoverSortOption(
    val field: String,
    val sql: String,
    val supportsTv: Boolean = true,
    val supportsMovie: Boolean = true,
    val defaultOrder: SortOrder = SortOrder.DESC
) {

    POPULARITY(
        field = "popularity",
        sql = "popularity"

    ),

    RELEASE_DATE(
        field = "primary_release_date",
        sql = "release_date",
        supportsTv = false
    ),

    FIRST_AIR_DATE(
        field = "first_air_date",
        sql = "release_date",
        supportsMovie = false
    ),

    VOTE_AVERAGE(
        field = "vote_average",
        sql = "vote_average"

    ),

    VOTE_COUNT(
        field = "vote_count",
        sql = "vote_count"
    ),

    REVENUE(
        field = "revenue",
        supportsTv = false,
        sql = "revenue"
    ),

    RUNTIME(
        field = "runtime",
        supportsMovie = false,
        sql = "runtime"
    )
}
