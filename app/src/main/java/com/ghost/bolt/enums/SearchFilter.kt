package com.ghost.bolt.enums

data class SearchFilter(
    val query: String = "",
    val mediaType: AppMediaType = AppMediaType.MOVIE, // Movie, TV, Anime
)




