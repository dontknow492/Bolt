package com.ghost.bolt.di

import android.content.Context
import com.ghost.bolt.database.AppDatabase
import com.ghost.bolt.database.dao.CastDao
import com.ghost.bolt.database.dao.CategoryDao
import com.ghost.bolt.database.dao.MediaDao
import com.ghost.bolt.database.dao.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideExpenseTrackerDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    // --- 3. DAO Providers ---
    // These allow you to inject DAOs directly into Repositories

    @Provides
    fun provideMediaDao(db: AppDatabase): MediaDao {
        return db.mediaDao()
    }

    @Provides
    fun provideRemoteKeysDao(db: AppDatabase): RemoteKeysDao {
        return db.remoteKeysDao()
    }

    @Provides
    fun provideCastDao(db: AppDatabase): CastDao {
        // You'll need to implement castDao() in AppDatabase abstract class first
        // return db.castDao()
        // For now, if you haven't added castDao to AppDatabase, comment this out.
        throw NotImplementedError("Register castDao in AppDatabase first")
    }

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao {
        return db.categoryDao()
    }

}