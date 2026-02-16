package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkKeywords(
    @SerialName("keywords") val movieKeywords: List<NetworkKeyword>? = null,
    @SerialName("results") val tvKeywords: List<NetworkKeyword>? = null
) {
    val all: List<NetworkKeyword> get() = movieKeywords ?: tvKeywords ?: emptyList()
}