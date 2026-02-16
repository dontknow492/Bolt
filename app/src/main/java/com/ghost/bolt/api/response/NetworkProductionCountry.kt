package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProductionCountry(
    @SerialName("iso_3166_1") val isoCode: String, // Note: Maps to your Entity ID
    @SerialName("name") val name: String
)