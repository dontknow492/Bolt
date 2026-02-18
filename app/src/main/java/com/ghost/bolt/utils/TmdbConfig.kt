package com.ghost.bolt.utils

import android.net.Uri
import com.ghost.bolt.enums.AppMediaType

object TmdbConfig {

    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

    // --- Backdrop Sizes ---
    enum class TmdbBackdropSize(val sizeCode: String) {
        W300("w300"),
        W780("w780"),
        W1280("w1280"),
        ORIGINAL("original")
    }


    // --- Poster Sizes ---
    enum class TMDbPosterSize(val sizeCode: String, val description: String) {
        W92("w92", "Very small thumbnails"),
        W154("w154", "Compact lists"),
        W342("w342", "Standard poster"),
        W500("w500", "High quality poster"),
        W780("w780", "Large poster"),
        ORIGINAL("original", "Full resolution");

        override fun toString(): String {
            return "$sizeCode - $description"
        }
    }

    fun getTmdbUrl(mediaType: AppMediaType, id: Int): String {
        return "https://www.themoviedb.org/${mediaType.name.lowercase()}/${id}"
    }

    // --- Backdrop URL ---
    fun getBackdropUrl(
        path: String?,
        size: TmdbBackdropSize = TmdbBackdropSize.W780
    ): String? {
        if (path == null) {
            return null
        }
        return buildUrl(path, size.sizeCode)
    }

//    fun getBackdropUrl(
//        path: String,
//        size: TmdbBackdropSize = TmdbBackdropSize.W780
//    ): String {
//        return buildUrl(path, size.sizeCode)
//    }

    fun getBackdropUri(
        path: String,
        size: TmdbBackdropSize = TmdbBackdropSize.W780
    ): Uri {
        return Uri.parse(getBackdropUrl(path, size))
    }

    // --- Poster URL ---
//    fun getPosterUrl(
//        path: String,
//        size: TMDbPosterSize = TMDbPosterSize.W500
//    ): String {
//        return buildUrl(path, size.sizeCode)
//    }

    fun getPosterUrl(path: String?, size: TMDbPosterSize = TMDbPosterSize.W342): String? {
        if (path == null) {
            return null
        }
        return buildUrl(path, size.sizeCode)
    }

    fun getPosterUri(
        path: String,
        size: TMDbPosterSize = TMDbPosterSize.W342
    ): Uri {
        return Uri.parse(getPosterUrl(path, size))
    }

    // --- Core Builder ---
    private fun buildUrl(path: String, sizeCode: String): String {
        val cleanPath = if (path.startsWith("/")) path else "/$path"
        return "$IMAGE_BASE_URL$sizeCode$cleanPath"
    }
}
