package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCredits(
    @SerialName("cast") val cast: List<NetworkCast>? = null,
    @SerialName("crew") val crew: List<NetworkCrew>? = null
)