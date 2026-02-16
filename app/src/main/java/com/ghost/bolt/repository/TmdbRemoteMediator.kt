package com.ghost.bolt.repository

import com.ghost.bolt.api.TmdbApi
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.tdmb.TMDbMediaCategory
import com.ghost.bolt.enums.tdmb.TmdbMediaType
import com.ghost.bolt.utils.mapper.toMediaEntity
import timber.log.Timber

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

        val tmdbMediaType = mediaType.value
        val tmdbCategoryPath = category.path

        Timber.tag("TMDB_FETCH").v(
            """
        🚀 Fetching from TMDB
        • MediaType: %s
        • Category: %s
        • Page: %d
        • Language: %s
        • Region: %s
        """.trimIndent(),
            tmdbMediaType,
            tmdbCategoryPath,
            page,
            language,
            region
        )

        val startTime = System.currentTimeMillis()

        val response = try {

            val result = when (category) {

                TMDbMediaCategory.TRENDING -> {
                    Timber.tag("TMDB_FETCH").v("Calling TRENDING endpoint")
                    api.getTrending(
                        mediaType = tmdbMediaType,
                        page = page,
                        language = language,
                    )
                }

                TMDbMediaCategory.TOP_RATED,
                TMDbMediaCategory.POPULAR,
                TMDbMediaCategory.NOW_PLAYING,
                TMDbMediaCategory.UPCOMING,
                TMDbMediaCategory.AIRING_TODAY,
                TMDbMediaCategory.ON_THE_AIR -> {

                    Timber.tag("TMDB_FETCH").v(
                        "Calling MEDIA LIST endpoint: %s",
                        tmdbCategoryPath
                    )

                    api.getMediaList(
                        mediaType = tmdbMediaType,
                        category = tmdbCategoryPath,
                        page = page,
                        language = language,
                        region = region
                    )
                }
            }

            val duration = System.currentTimeMillis() - startTime

            Timber.tag("TMDB_FETCH").v(
                """
            ✅ Success
            • Page: %d
            • Total Results: %d
            • Total Pages: %d
            • Took: %d ms
            """.trimIndent(),
                result.page,
                result.totalResults,
                result.totalPages,
                duration
            )

            result

        } catch (e: Exception) {

            Timber.tag("TMDB_FETCH").e(
                e,
                """
            ❌ Network Fetch Failed
            • MediaType: %s
            • Category: %s
            • Page: %d
            """.trimIndent(),
                tmdbMediaType,
                tmdbCategoryPath,
                page
            )

            throw e
        }

        val mapped = response.results.map { it.toMediaEntity() }

        Timber.tag("TMDB_FETCH").v(
            "🎬 Mapped %d results to MediaEntity",
            mapped.size
        )

        return mapped
    }

}