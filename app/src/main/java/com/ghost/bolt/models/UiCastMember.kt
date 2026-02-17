package com.ghost.bolt.models

/**
 * Represents a Cast Member for the UI, combining their profile with their specific role in this movie.
 */
data class UiCastMember(
    val id: Int,
    val name: String,
    val profilePath: String?,
    val characterName: String?,
    val creditOrder: Int,
    val department: String? = null // Useful for Crew separation if needed
)