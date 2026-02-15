package com.ghost.bolt.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ghost.bolt.database.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Upsert
    fun upsertCategory(category: CategoryEntity)

    @Query("SELECT * FROM Categories")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM Categories WHERE category_id = :categoryId")
    fun getCategoryById(categoryId: Int): Flow<CategoryEntity?>
}