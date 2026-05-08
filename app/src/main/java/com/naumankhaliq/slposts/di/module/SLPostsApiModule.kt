/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 *
 */

package com.naumankhaliq.slposts.di.module

import com.naumankhaliq.slposts.BuildConfig
import com.naumankhaliq.slposts.data.remote.api.SLPostsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * [SLPostsApiModule] that will handle api related dependencies.
 * [SLPostsAppModule] is included in this module
 */
@InstallIn(SingletonComponent::class)
@Module(includes = [SLPostsAppModule::class])
class SLPostsApiModule {

    /**
     * Provides retrofit service with Gson converter and okhttp client
     * @param okHttpClient of type [OkHttpClient]
     * @return [SLPostsService]
     */
    @Singleton
    @Provides
    fun provideRetrofitService(okHttpClient: OkHttpClient): SLPostsService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()
        .create(SLPostsService::class.java)
}
