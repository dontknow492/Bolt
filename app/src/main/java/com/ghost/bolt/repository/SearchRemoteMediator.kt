package com.ghost.bolt.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ghost.bolt.api.tmdb.TmdbApi

import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.RemoteKeyEntity
import com.ghost.bolt.enums.tdmb.TmdbMediaType
import com.ghost.bolt.utils.mapper.toMediaEntity
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val db: AppDatabase,
    private val api: TmdbApi,
    private val query: String,
    private val mediaType: TmdbMediaType,
    private val language: String = "en-US",
    private val includeAdult: Boolean = false
) : RemoteMediator<Int, MediaEntity>() {

    private val remoteKeyLabel = "search_query_$query"

    override suspend fun initialize(): InitializeAction {
        // For search, we might want to refresh more often, or valid for a short time
        val remoteKey = db.remoteKeysDao().getRemoteKeyByLabel(remoteKeyLabel)
        val lastUpdated = remoteKey?.lastUpdated ?: 0L
        val cacheTimeout = TimeUnit.HOURS.toMillis(1) // 1 hour cache for search results

        return if (System.currentTimeMillis() - lastUpdated < cacheTimeout) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MediaEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.remoteKeysDao().getRemoteKeyByLabel(remoteKeyLabel)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                }
            }

            Timber.d("Searching '$query', page $page")

            val entities = fetchFromNetwork(page)
            val endOfPaginationReached = entities.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().deleteRemoteKeyByLabel(remoteKeyLabel)
                }

                val nextKey = if (endOfPaginationReached) null else page + 1

                db.remoteKeysDao().upsertKeys(
                    listOf(RemoteKeyEntity(remoteKeyLabel, nextKey, System.currentTimeMillis()))
                )

                db.mediaDao().upsertMedia(entities)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: Exception) {
            Timber.e(e, "Search load failed")
            MediatorResult.Error(e)
        }
    }

    suspend fun fetchFromNetwork(page: Int): List<MediaEntity> {

        Timber.tag("TMDB_FETCH").v(
            """
        🚀 Fetching from TMDB
        • Query: %s
        • MediaType: %s
        • Page: %d
        • Language: %s
        """.trimIndent(),
            mediaType.value,
            query,
            page,
            language,
        )

        val startTime = System.currentTimeMillis()

        try {
            val duration: Long
            val mapped: List<MediaEntity>

            when (mediaType) {

                TmdbMediaType.MOVIE -> {

                    val response = api.searchMovies(
                        query = query,
                        includeAdult = includeAdult,
                        page = page,
                        language = language
                    )

                    duration = System.currentTimeMillis() - startTime

                    mapped = response.results.map { it.toMediaEntity() }
                }

                TmdbMediaType.TV -> {

                    val response = api.searchTv(
                        query = query,
                        page = page,
                        language = language,
                        includeAdult = includeAdult
                    )


                    duration = System.currentTimeMillis() - startTime

                    mapped = response.results.map { it.toMediaEntity() }
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
            • Query: %s
            • Page: %d
            """.trimIndent(),
                mediaType.value,
                query,
                page
            )

            throw e
        }
    }


}



