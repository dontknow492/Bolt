package com.ghost.bolt.api.response

import kotlinx.serialization.Serializable

@Serializable
data class TMDbDates(
    val maximum: String,
    val minimum: String
)