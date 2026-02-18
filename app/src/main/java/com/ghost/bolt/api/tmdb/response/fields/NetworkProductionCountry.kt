package com.ghost.bolt.api.tmdb.response.fields

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProductionCountry(
    @SerialName("iso_3166_1") val isoCode: String, // Note: Maps to your MediaEntityCard ID
    @SerialName("name") val name: String
)