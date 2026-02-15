package com.ghost.bolt.repository

import com.ghost.bolt.api.TmdbApi
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.tdmb.TMDbMediaCategory
import com.ghost.bolt.enums.tdmb.TmdbMediaType
import com.ghost.bolt.utils.mapper.toMediaEntity

class TmdbRemoteMediator(
    private val db: AppDatabase,
    private val api: TmdbApi,
    private val categoryId: Int,
    private val category: TMDbMediaCategory,
    private val mediaType: TmdbMediaType,
    private val region: String? = null,
    private val language: String = "en-US"
) : BaseMediaRemoteMediator(
    db = db,
    categoryId = categoryId,
    remoteKeyLabel = "${category.path}_${mediaType.name.lowercase()}"
) {

    override suspend fun fetchFromNetwork(page: Int): List<MediaEntity> {
        // Map your internal Enums to TMDB specific strings/paths
        val tmdbMediaType = mediaType.name
        val tmdbCategoryPath = category.path

        val response = when (category) {
            TMDbMediaCategory.TRENDING -> api.getTrending(
                mediaType = tmdbMediaType,
                timeWindow = tmdbCategoryPath,
                page = page,
                language = language
            )

            TMDbMediaCategory.TOP_RATED, TMDbMediaCategory.POPULAR, TMDbMediaCategory.NOW_PLAYING,
            TMDbMediaCategory.UPCOMING, TMDbMediaCategory.AIRING_TODAY, TMDbMediaCategory.ON_THE_AIR -> {
                api.getMediaList(
                    mediaType = tmdbMediaType,
                    category = tmdbCategoryPath,
                    page = page,
                    language = language,
                    region = region
                )
            }
        }



        return response.results.map { it.toMediaEntity() }
    }
}