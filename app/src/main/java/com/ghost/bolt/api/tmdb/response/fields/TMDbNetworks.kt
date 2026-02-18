package com.ghost.bolt.api.tmdb.response.fields

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDbNetworks(
    @SerialName("id") var id: Int,
    @SerialName("logo_path") var logoPath: String? = null,
    @SerialName("name") var name: String? = null,
    @SerialName("origin_country") var originCountry: String? = null

)