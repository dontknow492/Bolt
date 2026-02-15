package com.ghost.bolt.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef

@Dao
interface CastDao {

    @Query("SELECT * FROM Cast WHERE cast_id = :personId")
    suspend fun getPerson(personId: Int): CastEntity?

    // Find all movies featuring this actor
    @Transaction
    @Query(
        """
        SELECT * FROM Media 
        INNER JOIN MediaCast ON Media.id = MediaCast.media_id 
        WHERE MediaCast.cast_id = :personId 
        ORDER BY Media.release_date DESC
    """
    )
    suspend fun getMoviesForActor(personId: Int): List<MediaEntity>

    // --- UPSERT METHODS ---

    /**
     * Inserts or updates a list of cast members.
     */
    @Upsert
    suspend fun upsertCast(cast: List<CastEntity>)

    /**
     * Inserts or updates the relationship between movies and actors.
     */
    @Upsert
    suspend fun upsertMediaCastLinks(links: List<MediaCastCrossRef>)

//    suspend fun getMoviesForActor()

}