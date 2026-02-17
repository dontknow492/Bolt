package com.ghost.bolt.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ghost.bolt.database.dao.CastDao
import com.ghost.bolt.database.dao.CategoryDao
import com.ghost.bolt.database.dao.MediaDao
import com.ghost.bolt.database.dao.MediaDecompositionDao
import com.ghost.bolt.database.dao.RemoteKeysDao
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.CategoryEntity
import com.ghost.bolt.database.entity.GenreEntity
import com.ghost.bolt.database.entity.KeywordEntity
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.database.entity.ProductionCompanyEntity
import com.ghost.bolt.database.entity.ProductionCountryEntity
import com.ghost.bolt.database.entity.RemoteKeyEntity
import com.ghost.bolt.database.entity.SpokenLanguageEntity
import com.ghost.bolt.database.entity.cross_ref.MediaCastCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaCategoryCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaGenreCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaKeywordCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaProductionCompanyCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaProductionCountryCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaRecommendationCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSimilarCrossRef
import com.ghost.bolt.database.entity.cross_ref.MediaSpokenLanguageCrossRef

@Database(
    entities = [
        // 1. Core Tables
        MediaEntity::class,
        RemoteKeyEntity::class,
        CategoryEntity::class,

        // 2. Metadata Tables
        GenreEntity::class,
        KeywordEntity::class,
        ProductionCompanyEntity::class,
        ProductionCountryEntity::class,
        SpokenLanguageEntity::class,
        CastEntity::class,

        // 3. Cross-Reference (Link) Tables
        MediaSimilarCrossRef::class,
        MediaRecommendationCrossRef::class,
        MediaCategoryCrossRef::class,
        MediaGenreCrossRef::class,
        MediaCastCrossRef::class,
        MediaKeywordCrossRef::class,              // Make sure you created this
        MediaProductionCompanyCrossRef::class,    // Make sure you created this
        MediaProductionCountryCrossRef::class,    // Make sure you created this
        MediaSpokenLanguageCrossRef::class        // Make sure you created this
    ],
    version = 1, // Change this if you ever update the schema later
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mediaDao(): MediaDao
    abstract fun remoteKeysDao(): RemoteKeysDao
    abstract fun castDao(): CastDao

    abstract fun categoryDao(): CategoryDao

    abstract fun mediaDecompositionDao(): MediaDecompositionDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = "app_database"
                )
                    .createFromAsset("database/category.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}