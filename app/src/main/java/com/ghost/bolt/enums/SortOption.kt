package com.ghost.bolt.enums

enum class SortOption(val dbColumn: String) {
    POPULARITY("popularity"),
    VOTE_AVERAGE("vote_average"),
    RELEASE_DATE("release_date"),
    REVENUE("revenue"),
    TITLE("title")
}