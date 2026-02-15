package com.ghost.bolt.repository

//import com.ghost.bolt.database.query.ActorMovieFilter
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.ghost.bolt.api.TmdbApi
import com.ghost.bolt.api.response.toCastEntity
import com.ghost.bolt.api.response.toMediaCastCrossRef
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.entity.MediaDetail
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSimilarCrossRef
import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.AppendToResponseItem
import com.ghost.bolt.enums.MediaProvider
import com.ghost.bolt.enums.toTmdbCategoryOrNull
import com.ghost.bolt.enums.toTmdbMediaType
import com.ghost.bolt.exceptions.InvalidMediaCategoryException
import com.ghost.bolt.exceptions.InvalidMediaTypeException
import com.ghost.bolt.utils.TmdbQueryUtils
import com.ghost.bolt.utils.mapper.toMediaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val db: AppDatabase,
    private val api: TmdbApi
) {
    private val mediaDao = db.mediaDao()
    private val castDao = db.castDao()
    private val categoryDao = db.categoryDao()


    // --- 1. HOME SCREEN PAGERS ---
    // These create the infinite streams for your LazyRows

    fun getCategoryMediaList(
        category: AppCategory,
        mediaType: AppMediaType
    ): Flow<PagingData<MediaEntity>> {
        return createPager(category, mediaType)
    }


    // Helper to create Pagers cleanly
    @OptIn(ExperimentalPagingApi::class)
    private fun createPager(
        category: AppCategory,
        mediaType: AppMediaType
    ): Flow<PagingData<MediaEntity>> {

        val categoryId = category.id

        return when (mediaType) {

            AppMediaType.MOVIE,
            AppMediaType.TV -> {

                val tmdbMediaType = mediaType.toTmdbMediaType()
                    ?: throw InvalidMediaTypeException(MediaProvider.TMDB, mediaType)

                val tmdbCategory = category.toTmdbCategoryOrNull()
                    ?: throw InvalidMediaCategoryException(MediaProvider.TMDB, category)

                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = true
                    ),
                    remoteMediator = TmdbRemoteMediator(
                        db = db,
                        api = api,
                        categoryId = categoryId,
                        category = tmdbCategory,
                        mediaType = tmdbMediaType
                    ),
                    pagingSourceFactory = {
                        mediaDao.getMediaByCategoryId(categoryId, mediaType.name)
                    }
                ).flow
            }

            AppMediaType.ANIME -> {
                throw InvalidMediaTypeException(MediaProvider.TMDB, mediaType)
            }
        }
    }

    // --- 2. DETAIL PAGE ---
    // This follows the "Network-Bound Resource" pattern:
    // Show DB data first, then update from Network in background.

    /**
     * Returns a Flow of the MediaDetail.
     * This allows the UI to show cached data immediately and update automatically
     * when the network refresh completes.
     */
    fun getMediaDetail(mediaId: Int): Flow<MediaDetail?> {
        return mediaDao.getMediaDetail(mediaId)
    }

    /**
     * Fetches everything for a movie in ONE network call and updates the DB.
     */
    suspend fun refreshMediaDetail(mediaId: Int) = withContext(Dispatchers.IO) {
        try {
            // 1. One API call to get Movie + Cast + Recommendations
            val networkMovie = api.getMovie(
                movieId = mediaId,
                appendToResponse = TmdbQueryUtils.buildAppendToResponse(
                    AppendToResponseItem.CREDITS,
                    AppendToResponseItem.RECOMMENDATIONS,
                    AppendToResponseItem.VIDEOS
                )
            )

            // 2. Wrap everything in a transaction for data integrity
            db.withTransaction {
                // A. Update the core movie data (including budget, runtime, etc.)
                val coreEntity = networkMovie.toMediaEntity()
                mediaDao.upsertMedia(listOf(coreEntity))

                // B. Handle Cast & Credits
                networkMovie.credits?.cast?.let { castList ->
                    val castEntities = castList.map { it.toCastEntity() }
                    val crossRefs = castList.map { it.toMediaCastCrossRef(mediaId) }

                    castDao.upsertCast(castEntities)
                    castDao.upsertMediaCastLinks(crossRefs)
                }

                // C. Handle Recommendations
                networkMovie.recommendations?.results?.let { recs ->
                    // First, save the recommended movies as "shallow" records
                    val recEntities = recs.map { it.toMediaEntity() }
                    mediaDao.upsertMedia(recEntities)

                    // Second, link them to the source movie
                    val recLinks = recs.mapIndexed { index, rec ->
                        MediaRecommendationCrossRef(
                            sourceMediaId = mediaId,
                            targetMediaId = rec.id,
                            displayOrder = index
                        )
                    }
                    mediaDao.upsertRecommendations(recLinks)
                }

                // D. Handle Similars
                networkMovie.similar?.results?.let { recs ->
                    // First, save the recommended movies as "shallow" records
                    val similarEntities = recs.map { it.toMediaEntity() }
                    mediaDao.upsertMedia(similarEntities)

                    // Second, link them to the source movie
                    val similarLinks = recs.mapIndexed { index, rec ->
                        MediaSimilarCrossRef(
                            sourceMediaId = mediaId,
                            targetMediaId = rec.id,
                            displayOrder = index
                        )
                    }
                    mediaDao.upsertSimilarMovies(similarLinks)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- 3. SEARCH ---
    fun searchMovies(query: String): Flow<PagingData<MediaEntity>> {
        // For search, we typically don't use a RemoteMediator unless
        // you want to cache search results.
        // Here we just search the local DB (which is huge anyway!)
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { mediaDao.searchByName(query) }
        ).flow
    }

}