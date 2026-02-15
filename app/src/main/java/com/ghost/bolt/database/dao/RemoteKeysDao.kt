package com.ghost.bolt.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ghost.bolt.database.entity.RemoteKeyEntity

@Dao
interface RemoteKeysDao {

    @Upsert
    suspend fun upsertKeys(remoteKeys: List<RemoteKeyEntity>)

    @Query("SELECT * FROM RemoteKeys WHERE label_id = :labelId")
    suspend fun getRemoteKeyByLabel(labelId: String): RemoteKeyEntity?

    @Query("DELETE FROM RemoteKeys WHERE label_id = :labelId")
    suspend fun deleteRemoteKeyByLabel(labelId: String)
}