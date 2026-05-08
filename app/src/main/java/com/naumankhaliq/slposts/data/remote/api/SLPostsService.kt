/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.data.remote.api

import com.naumankhaliq.slposts.model.response.posts.Post
import retrofit2.Response
import retrofit2.http.GET

/**
 * Service to fetch Posts data from server [BuildConfig.BASE_URL].
 */
interface SLPostsService {

    @GET("/posts")
    suspend fun getPosts(): Response<List<Post>>
}
