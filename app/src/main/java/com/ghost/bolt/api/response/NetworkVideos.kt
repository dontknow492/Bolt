package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkVideos(
    @SerialName("results") val results: List<NetworkVideo>? = null
)