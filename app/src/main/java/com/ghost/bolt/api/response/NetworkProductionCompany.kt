package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkProductionCompany(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("logo_path") val logoPath: String? = null,
    @SerialName("origin_country") val originCountry: String
)