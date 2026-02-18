package com.ghost.bolt.api.tmdb.response.fields

import kotlinx.serialization.Serializable

@Serializable
data class TMDbDates(
    val maximum: String,
    val minimum: String
)