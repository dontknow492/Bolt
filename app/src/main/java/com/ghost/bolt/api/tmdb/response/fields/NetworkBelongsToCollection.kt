package com.ghost.bolt.api.tmdb.response.fields

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBelongsToCollection(

    @SerialName("id") var id: Int? = null,
    @SerialName("name") var name: String? = null,
    @SerialName("poster_path") var posterPath: String? = null,
    @SerialName("backdrop_path") var backdropPath: String? = null

)