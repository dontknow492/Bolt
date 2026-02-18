package com.ghost.bolt.repository

import com.ghost.bolt.api.tmdb.TmdbApi
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

        Timber.tag("TMDB_FETCH").v(
            """
        🚀 Fetching from TMDB
        • MediaType: %s
        • Category: %s
        • Page: %d
        • Language: %s
        • Region: %s
        """.trimIndent(),
            mediaType.value,
            category.path,
            page,
            language,
            region
        )

        val startTime = System.currentTimeMillis()

        try {
            val duration: Long
            val mapped: List<MediaEntity>

            when (mediaType) {

                TmdbMediaType.MOVIE -> {

                    val response = when (category) {

                        TMDbMediaCategory.TRENDING ->
                            api.getTrendingMovies(
                                page = page,
                                language = language
                            )

                        else ->
                            api.getMovieList(
                                category = category.path,
                                page = page,
                                language = language,
                                region = region
                            )
                    }

                    duration = System.currentTimeMillis() - startTime

                    mapped = response.results.map { it.toMediaEntity() }
                }

                TmdbMediaType.TV -> {

                    val response = when (category) {

                        TMDbMediaCategory.TRENDING ->
                            api.getTrendingTv(
                                page = page,
                                language = language
                            )

                        else ->
                            api.getTvList(
                                category = category.path,
                                page = page,
                                language = language,
                                region = region
                            )
                    }

                    duration = System.currentTimeMillis() - startTime

                    mapped = response.results.map { it.toMediaEntity() }
                }

                else -> {
                    TODO()
                }
            }

            Timber.tag("TMDB_FETCH").v(
                """
            ✅ Success
            • Page: %d
            • Results: %d
            • Took: %d ms
            """.trimIndent(),
                page,
                mapped.size,
                duration
            )

            return mapped

        } catch (e: Exception) {

            Timber.tag("TMDB_FETCH").e(
                e,
                """
            ❌ Network Fetch Failed
            • MediaType: %s
            • Category: %s
            • Page: %d
            """.trimIndent(),
                mediaType.value,
                category.path,
                page
            )

            throw e
        }
    }


}