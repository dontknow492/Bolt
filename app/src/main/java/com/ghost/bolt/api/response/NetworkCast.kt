package com.ghost.bolt.api.response

import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCast(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("character") val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("order") val order: Int,
    @SerialName("known_for_department") val knownForDepartment: String? = null
)


fun NetworkCast.toCastEntity(): CastEntity {
    return CastEntity(
        castId = id,
        name = name,
        profilePath = profilePath,
        knownForDepartment = knownForDepartment,
    )
}

fun NetworkCast.toMediaCastCrossRef(mediaId: Int): MediaCastCrossRef {
    return MediaCastCrossRef(
        mediaId = mediaId,
        castId = id,
        characterName = character,
        creditOrder = order
    )
}