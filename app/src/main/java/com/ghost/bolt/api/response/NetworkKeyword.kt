package com.ghost.bolt.api.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkKeyword(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)