package com.ghost.bolt.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.RemoteKeyEntity
import com.ghost.bolt.database.entity.cross_ref.MediaCategoryCrossRef
import com.ghost.bolt.enums.RefreshFrequency
import timber.log.Timber
import java.time.Instant

@OptIn(ExperimentalPagingApi::class)
abstract class BaseMediaRemoteMediator(
    private val db: AppDatabase,
    private val categoryId: Int,
    private val remoteKeyLabel: String // e.g. "popular_movies" or "trending_anime"
) : RemoteMediator<Int, MediaEntity>() {

    /**
     * Subclasses must implement this to call their specific API (TMDb, AniList, etc.)
     * and map the response to a List of MediaEntity.
     */
    abstract suspend fun fetchFromNetwork(page: Int): List<MediaEntity>

    override suspend fun initialize(): InitializeAction {
        val category = db.categoryDao().getCategoryById(categoryId)

        val refreshFrequency = category?.refreshFrequency
            ?: RefreshFrequency.DAILY // fallback safety

        val remoteKey = db.remoteKeysDao()
            .getRemoteKeyByLabel(remoteKeyLabel)

        val lastUpdatedMillis = remoteKey?.lastUpdated ?: 0L

        val now = Instant.now()

        val shouldRefresh = if (lastUpdatedMillis == 0L) {
            true
        } else {
            val lastUpdated = Instant.ofEpochMilli(lastUpdatedMillis)
            val nextAllowedRefresh = refreshFrequency.nextRefreshFrom(lastUpdated)
            now.isAfter(nextAllowedRefresh)
        }

        return if (shouldRefresh) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
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
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    remoteKey.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }

            }

            Timber.d("[%s] Loading page %d", remoteKeyLabel, page)

            // Call the abstract network fetch
            val entities = fetchFromNetwork(page)
            val endOfPaginationReached = entities.isEmpty()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.mediaDao().clearCategoryLinks(categoryId)
                    db.remoteKeysDao().deleteRemoteKeyByLabel(remoteKeyLabel)
                }

                val nextKey = if (endOfPaginationReached) null else page + 1

                db.remoteKeysDao().upsertKeys(
                    listOf(RemoteKeyEntity(remoteKeyLabel, nextKey, System.currentTimeMillis()))
                )

                db.mediaDao().upsertMedia(entities)

                val startingPos = if (loadType == LoadType.REFRESH) 0
                else (db.mediaDao().getLastPositionByCategory(categoryId) ?: 0) + 1

                val links = entities.mapIndexed { index, media ->
                    MediaCategoryCrossRef(
                        media.id,
                        media.mediaType,
                        media.mediaSource,
                        categoryId,
                        startingPos + index
                    )
                }
                db.mediaDao().upsertMediaCategoryLinks(links)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Timber.e(e, "[%s] Load failed", remoteKeyLabel)
            MediatorResult.Error(e)
        }
    }
}


