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
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.DiscoverFilter
import com.ghost.bolt.enums.LogicMode
import com.ghost.bolt.enums.maxDate
import com.ghost.bolt.enums.minDate
import com.ghost.bolt.utils.mapper.toMediaEntity
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class DiscoverRemoteMediator(
    private val db: AppDatabase,
    private val api: TmdbApi,
    private val filter: DiscoverFilter,
    private val language: String = "en-US",
    private val includeAdult: Boolean = false
) : RemoteMediator<Int, MediaEntity>() {

    private val remoteKeyLabel = buildRemoteKeyLabel()

    override suspend fun initialize(): InitializeAction {
        val remoteKey = db.remoteKeysDao().getRemoteKeyByLabel(remoteKeyLabel)
        val lastUpdated = remoteKey?.lastUpdated ?: 0L

        // Discover cache lasts longer than search
        val cacheTimeout = TimeUnit.HOURS.toMillis(6)

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
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)

                LoadType.APPEND -> {
                    val remoteKey =
                        db.remoteKeysDao().getRemoteKeyByLabel(remoteKeyLabel)

                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKey != null
                        )
                }
            }

            Timber.d("🎬 Discover page $page for $remoteKeyLabel")

            val entities = fetchFromNetwork(page)
            val endOfPaginationReached = entities.isEmpty()

            Timber.d("🎬 Response Enitity: ${entities.size} for page:$page - $remoteKeyLabel")

            db.withTransaction {

                if (loadType == LoadType.REFRESH) {
                    db.remoteKeysDao().deleteRemoteKeyByLabel(remoteKeyLabel)
                }

                val nextKey =
                    if (endOfPaginationReached) null else page + 1

                db.remoteKeysDao().upsertKeys(
                    listOf(
                        RemoteKeyEntity(
                            labelId = remoteKeyLabel,
                            nextPage = nextKey,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                )

                db.mediaDao().upsertMedia(entities)
            }

            MediatorResult.Success(endOfPaginationReached)

        } catch (e: Exception) {
            Timber.e(e, "❌ Discover load failed")
            MediatorResult.Error(e)
        }
    }

    private suspend fun fetchFromNetwork(page: Int): List<MediaEntity> {

        val startTime = System.currentTimeMillis()

        val genresParam = filter.genres.takeIf { it.isNotEmpty() }?.let {
            when (filter.genreLogic) {
                LogicMode.OR -> it.joinToString(",")
                LogicMode.AND -> it.joinToString("|")
            }
        }

        val castParam = filter.cast.takeIf { it.isNotEmpty() }
            ?.joinToString(",")

        val keywordsParam = filter.keywords.takeIf { it.isNotEmpty() }
            ?.joinToString(",")

        return when (filter.mediaType) {

            AppMediaType.MOVIE -> {

                val response = api.discoverMovie(
                    page = page,
                    language = language,
                    includeAdult = includeAdult,
                    withGenres = genresParam,
                    withCast = castParam,
                    withKeywords = keywordsParam,
                    voteAverageGte = filter.minVote,
                    releaseDateGte = filter.minDate(),
                    releaseDateLte = filter.maxDate(),
                    sortBy = filter.sortOption.field
                )

                val duration = System.currentTimeMillis() - startTime

                Timber.d(
                    "✅ Movie discover page $page, " +
                            "${response.results.size} results, " +
                            "took ${duration}ms"
                )

                response.results.map { it.toMediaEntity() }
            }

            AppMediaType.TV -> {

                val response = api.discoverTv(
                    page = page,
                    language = language,
                    includeAdult = includeAdult,
                    withGenres = genresParam,
                    withCast = castParam,
                    withKeywords = keywordsParam,
                    voteAverageGte = filter.minVote,
                    firstAirDateGte = filter.minDate(),
                    firstAirDateLte = filter.maxDate(),
                    sortBy = filter.sortOption.field
                )

                val duration = System.currentTimeMillis() - startTime

                Timber.d(
                    "✅ TV discover page $page, " +
                            "${response.results.size} results, " +
                            "took ${duration}ms"
                )

                response.results.map { it.toMediaEntity() }
            }

            AppMediaType.ANIME -> TODO()
        }
    }

    /**
     * Unique key so different filters don't mix pages.
     */
    private fun buildRemoteKeyLabel(): String {
        return buildString {
            append("discover_")
            append(filter.mediaType.name)
            append("_g${filter.genres.hashCode()}")
            append("_c${filter.cast.hashCode()}")
            append("_k${filter.keywords.hashCode()}")
            append("_v${filter.minVote}")
            append("_d${filter.minDate()}_${filter.maxDate()}")
            append("_s${filter.sortOption.field}")
        }
    }
}