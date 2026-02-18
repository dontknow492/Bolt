package com.ghost.bolt.api.tmdb.response.fields

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchProvider(

    @SerialName("logo_path") var logoPath: String? = null,
    @SerialName("provider_id") var providerId: Int,
    @SerialName("provider_name") var providerName: String,
    @SerialName("display_priority") var displayPriority: Int? = null

)