package com.ghost.bolt.repository

//import com.ghost.bolt.database.query.ActorMovieFilter
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.room.withTransaction
import com.ghost.bolt.api.tmdb.TmdbApi
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.Converters
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.AppendToResponseItem
import com.ghost.bolt.enums.DiscoverFilter
import com.ghost.bolt.enums.MediaProvider
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.enums.SearchFilter
import com.ghost.bolt.enums.tdmb.TmdbMediaType
import com.ghost.bolt.enums.toTmdbCategoryOrNull
import com.ghost.bolt.enums.toTmdbMediaType
import com.ghost.bolt.exceptions.InvalidMediaCategoryException
import com.ghost.bolt.exceptions.InvalidMediaTypeException
import com.ghost.bolt.models.UiMediaDetail
import com.ghost.bolt.models.toDecomposition
import com.ghost.bolt.utils.DiscoverQueryBuilder
import com.ghost.bolt.utils.SearchQueryBuilder
import com.ghost.bolt.utils.TmdbQueryUtils
import com.ghost.bolt.utils.mapper.toUiMediaDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InvalidObjectException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepository @Inject constructor(
    private val db: AppDatabase,
    private val tmdbApi: TmdbApi
) {
    private val converter: Converters = Converters()
    private val mediaDao = db.mediaDao()
    private val castDao = db.castDao()
    private val categoryDao = db.categoryDao()
    private val decompositionDao = db.mediaDecompositionDao()


    // --- 1. HOME SCREEN PAGERS ---
    // These create the infinite streams for your LazyRows

    fun getCategoryMediaList(
        category: AppCategory,
        mediaType: AppMediaType,
        mediaSource: MediaSource,
    ): Flow<PagingData<MediaEntity>> {
        return createPager(category, mediaType, mediaSource)
    }


    // Helper to create Pagers cleanly
    @OptIn(ExperimentalPagingApi::class)
    private fun createPager(
        category: AppCategory,
        mediaType: AppMediaType,
        mediaSource: MediaSource,
    ): Flow<PagingData<MediaEntity>> {

        val categoryId = category.id

        return when (mediaSource) {

            MediaSource.TMDB -> {

                val tmdbMediaType = mediaType.toTmdbMediaType()
                    ?: throw InvalidMediaTypeException(MediaProvider.TMDB, mediaType)

                val tmdbCategory = category.toTmdbCategoryOrNull()
                    ?: throw InvalidMediaCategoryException(MediaProvider.TMDB, category)

                Pager(
                    config = PagingConfig(
                        pageSize = 20,
                        // Fix: Disable placeholders to prevent key-generation from triggering loads
                        enablePlaceholders = false,
                        // Fix: Load 3x pages initially to fill screen and prevent immediate 'append'
                        initialLoadSize = 60,
                        prefetchDistance = 10
                    ),
                    remoteMediator = TmdbRemoteMediator(
                        db = db,
                        api = tmdbApi,
                        categoryId = categoryId,
                        category = tmdbCategory,
                        mediaType = tmdbMediaType
                    ),
                    pagingSourceFactory = {
                        mediaDao.getMediaByCategoryId(
                            categoryId = categoryId,
                            mediaType = mediaType.name
                        )
                    }
                ).flow
            }

            MediaSource.ANILIST -> {
                TODO("AniList pager not implemented yet")
            }

            else -> {
                throw UnsupportedOperationException("Unsupported media source: $mediaSource")
            }
        }
    }

    fun refreshCategory(category: AppCategory, mediaType: AppMediaType, mediaSource: MediaSource) {
        TODO("Not yet implemented")
    }


    /**
     * Fetches everything for a movie in ONE network call and updates the DB.
     */
    suspend fun refreshMediaDetail(
        mediaId: Int,
        mediaType: AppMediaType,
        mediaSource: MediaSource
    ) = withContext(Dispatchers.IO) {
        Timber.i("Refreshing media detail for ID: %d", mediaId)
        try {
            // 1. One API call to get Movie + Cast + Recommendations
            val mediaEntity = mediaDao.getMedia(mediaId, mediaType.name, mediaSource.name)
                ?: throw IllegalArgumentException("Media not found")

            when (val source = mediaEntity.mediaSource) {
                MediaSource.TMDB -> {
                    val decomposition = when (mediaEntity.mediaType) {
                        AppMediaType.MOVIE -> {
                            val details = tmdbApi.getMovieDetails(
                                id = mediaId,
                                appendToResponse = TmdbQueryUtils.buildAppendToResponse(
                                    AppendToResponseItem.CREDITS,
                                    AppendToResponseItem.RECOMMENDATIONS,
                                    AppendToResponseItem.VIDEOS,
                                    AppendToResponseItem.SIMILAR,
                                    AppendToResponseItem.KEYWORDS
                                )
                            )
                            details.toDecomposition(converter)
                        }

                        AppMediaType.TV -> {
                            val details = tmdbApi.getTvDetails(
                                id = mediaId,
                                appendToResponse = TmdbQueryUtils.buildAppendToResponse(
                                    AppendToResponseItem.CREDITS,
                                    AppendToResponseItem.RECOMMENDATIONS,
                                    AppendToResponseItem.SIMILAR,
                                    AppendToResponseItem.VIDEOS,
                                    AppendToResponseItem.KEYWORDS

                                )
                            )
                            details.toDecomposition(converter)
                        }

                        AppMediaType.ANIME -> TODO()
                    }

                    // 2. Wrap everything in a transaction for data integrity
                    db.withTransaction {
                        // A. Update the core movie data (including budget, runtime, etc.)
                        val coreEntity = decomposition
                        decompositionDao.insertDecomposition(coreEntity)

                    }
                }

                MediaSource.ANILIST -> TODO()
            }


        } catch (e: Exception) {
            e.printStackTrace()
            Timber.e(e, "Error refreshing media detail")
        }
    }


    /**
     * Returns a Flow of the MediaDetail.
     * This allows the UI to show cached data immediately and update automatically
     * when the network refresh completes.
     */
    suspend fun getUiMediaDetail(
        mediaId: Int,
        mediaType: AppMediaType,
        mediaSource: MediaSource
    ): Flow<UiMediaDetail?> {
        try {

            return mediaDao.getMediaDetailFlow(mediaId, mediaType.name, mediaSource.name)
                .onStart {
                    val mediaEntity =
                        mediaDao.getMediaDetail(mediaId, mediaType.name, mediaSource.name)
                            ?: throw InvalidObjectException("Invalid argurment error")

                    val shouldFetch = mediaEntity.genres.isEmpty()

                    if (shouldFetch) {
                        refreshMediaDetail(mediaId, mediaType, mediaSource)
                    }
                }
                .map { detail ->
                    if (detail == null) return@map null

                    val castLinks = mediaDao.getCastCrossRefs(mediaId)
                    val recLinks = mediaDao.getRecommendationCrossRefs(mediaId)
                    val simLinks = mediaDao.getSimilarCrossRefs(mediaId)

                    detail.toUiMediaDetail(castLinks, recLinks, simLinks)
                }
                .flowOn(Dispatchers.IO)
        } catch (e: Exception) {
            Timber.e(e, "Error getting media detail")
            throw e
        }

        // Ensure DB work happens on IO thread
    }


    @OptIn(ExperimentalPagingApi::class)
    fun searchMediaWithFilter(filter: SearchFilter): Flow<PagingData<MediaEntity>> {
        val query = SearchQueryBuilder.build(filter)
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                // Fix: Disable placeholders to prevent key-generation from triggering loads
                enablePlaceholders = false,
                // Fix: Load 3x pages initially to fill screen and prevent immediate 'append'
                initialLoadSize = 60,
                prefetchDistance = 10
            ),
            remoteMediator = SearchRemoteMediator(
                db = db,
                api = tmdbApi,
                query = filter.query,
                mediaType = TmdbMediaType.TV
            ),
            pagingSourceFactory = { mediaDao.advancedSearch(query) }
        ).flow

    }


    @OptIn(ExperimentalPagingApi::class)
    fun discoverMedia(filter: DiscoverFilter): Flow<PagingData<MediaEntity>> {
        val query = DiscoverQueryBuilder.build(filter)
        Timber.d("Discover Query: ${query.sql}, arg: ${query.argCount}")
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
                prefetchDistance = 1
            ),
            remoteMediator = DiscoverRemoteMediator(
                db = db,
                api = tmdbApi,
                filter = filter
            ),
            pagingSourceFactory = {
                mediaDao.getDiscoverResults(query)
            }
        ).flow
    }


}
