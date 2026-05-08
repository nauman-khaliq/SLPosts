/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.naumankhaliq.slposts.data.local.SLPostsDatabase
import javax.inject.Singleton

/**
 * [SLPostsDatabaseModule] which will provide database related dependencies
 */
@InstallIn(SingletonComponent::class)
@Module
class SLPostsDatabaseModule {

    /**
     * Provides [SLPostsDatabase] dependency
     * @param application of type [Application]
     * @return [SLPostsDatabase]
     */
    @Singleton
    @Provides
    fun provideDatabase(application: Application) = SLPostsDatabase.getInstance(application)

    /**
     * Provides [PostsDao] dependency
     * @param database of type [SLPostsDatabase]
     * @return [PostsDao]
     */
    @Singleton
    @Provides
    fun providePostsDao(database: SLPostsDatabase) = database.getPostsDao()

    /**
     * Provides [FavouritePostsDao] dependency
     * @param database of type [SLPostsDatabase]
     * @return [FavouritePostsDao]
     */
    @Singleton
    @Provides
    fun provideFavouritePostsDao(database: SLPostsDatabase) = database.getFavouritePostsDao()

}
