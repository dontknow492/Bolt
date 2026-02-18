package com.ghost.bolt.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.ghost.bolt.database.entity.MediaDetail
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaCategoryCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSimilarCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {

    // --- 1. HOME SCREEN (Paging) ---
    // Fetches movies for a horizontal row (e.g. Popular), ordered by API position

    @Query("SELECT * FROM Media WHERE id = :mediaId AND media_type = :mediaType AND media_source = :mediaSource")
    suspend fun getMedia(mediaId: Int, mediaType: String, mediaSource: String): MediaEntity?

    @Transaction
    @Query(
        """
    SELECT * FROM Media 
    INNER JOIN MediaCategories 
        ON Media.id = MediaCategories.media_id 
    WHERE MediaCategories.category_id = :categoryId
        AND (:mediaType IS NULL OR Media.media_type = :mediaType)
    ORDER BY MediaCategories.position_in_category ASC
    """
    )
    fun getMediaByCategoryId(
        categoryId: Int,
        mediaType: String?
    ): PagingSource<Int, MediaEntity>


    // --- 2. DETAIL PAGE ---
    // Fetches everything (Cast, Genres, etc.) in one transaction
    @Transaction
    @Query("SELECT * FROM Media WHERE id = :mediaId AND media_type = :mediaType AND media_source = :mediaSource")
    fun getMediaDetailFlow(mediaId: Int, mediaType: String, mediaSource: String): Flow<MediaDetail?>

    // --- 3. SEARCH & DISCOVERY (Complex) ---
    // Use this for simple name search
    @Query(
        """
        SELECT * FROM Media 
        WHERE title LIKE '%' || :query || '%' 
        ORDER BY popularity DESC
    """
    )
    fun searchByName(query: String): PagingSource<Int, MediaEntity>

    // Use this for the "Advanced Filter" screen
    // You will build the SimpleSQLiteQuery in your Repository based on UI state
    @RawQuery(observedEntities = [MediaEntity::class])
    fun advancedSearch(query: SupportSQLiteQuery): PagingSource<Int, MediaEntity>

    // --- 4. REMOTE MEDIATOR HELPERS ---

    @Upsert
    suspend fun upsertMedia(media: List<MediaEntity>)

    @Upsert
    suspend fun upsertMediaCategoryLinks(links: List<MediaCategoryCrossRef>)

    @Query("DELETE FROM MediaCategories WHERE category_id = :categoryId")
    suspend fun clearCategoryLinks(categoryId: Int)

    @Query("SELECT MAX(position_in_category) FROM MediaCategories WHERE category_id = :categoryId")
    suspend fun getLastPositionByCategory(categoryId: Int): Int?


    /**
     * Inserts or updates the recommendation links between movies.
     * This connects a Source Movie to many Target Movies.
     */
    @Upsert
    suspend fun upsertRecommendations(recommendations: List<MediaRecommendationCrossRef>)

    /**
     * Inserts or updates the similar movies links.
     */
    @Upsert
    suspend fun upsertSimilarMovies(similar: List<MediaSimilarCrossRef>)


    @Query("SELECT * FROM MediaCast WHERE media_id = :mediaId")
    suspend fun getCastCrossRefs(mediaId: Int): List<MediaCastCrossRef>

    @Query("SELECT * FROM MediaRecommendations WHERE source_media_id = :mediaId")
    suspend fun getRecommendationCrossRefs(mediaId: Int): List<MediaRecommendationCrossRef>

    @Query("SELECT * FROM MediaSimilar WHERE source_media_id = :mediaId")
    suspend fun getSimilarCrossRefs(mediaId: Int): List<MediaSimilarCrossRef>


}


