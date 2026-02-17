package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkVideo(
    @SerialName("id") val id: String,
    @SerialName("key") val key: String,
    @SerialName("name") val name: String,
    @SerialName("site") val site: String,
    @SerialName("type") val type: String, // "Trailer", "Teaser", "Featurette"
    @SerialName("iso_639_1") var iso6391: String? = null,
    @SerialName("iso_3166_1") var iso31661: String? = null,
    @SerialName("size") var size: Int? = null,
    @SerialName("official") var official: Boolean? = null,
    @SerialName("published_at") var publishedAt: String? = null,
)