package com.ghost.bolt.api.tmdb.response.fields

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkSpokenLanguage(
    @SerialName("iso_639_1") val isoCode: String, // Note: Maps to your MediaEntityCard ID
    @SerialName("name") val name: String,
    @SerialName("english_name") val englishName: String

)