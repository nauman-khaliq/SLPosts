/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 *
 */

package com.naumankhaliq.slposts.data.repository

import com.naumankhaliq.slposts.data.local.dao.FavouritePostsDao
import com.naumankhaliq.slposts.data.local.dao.PostsDao
import com.naumankhaliq.slposts.data.remote.api.SLPostsService
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.utils.MockResponseFileReader
import com.naumankhaliq.slposts.utils.TestRetrofitHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Response
import javax.inject.Inject

/**
 * Singleton repository for fetching data from remote and storing it in database
 * for offline capability. This is Single source of data.
 */
@ExperimentalCoroutinesApi
open class FakeSLPostsRepository @Inject constructor(
    private val postsDao: PostsDao,
    private val favouritePostsDao: FavouritePostsDao,
) : SLPostsRepository {

    var testApiService: SLPostsService
    private var mockWebServer: MockWebServer = MockWebServer()

    init {
        mockWebServer.start()
        testApiService = TestRetrofitHelper.getTestRetrofitServiceForMockServer<SLPostsService>(mockWebServer)
    }

    override fun getPosts(): Flow<Resource<List<Post>>> {
        return object: NetworkAndDbBoundRepository<List<Post>, List<Post>>() {
            override suspend fun saveRemoteData(response: List<Post>) {
                val favMovies = favouritePostsDao.getAllFavouritePosts()
                val favMovList = ArrayList<Post>()
                favMovies.forEach {
                    favMovList.add(it.post)
                }
                //moviesDao.upsertMovies(listOf(Movie(trackId = 1, movieName = "test1", shortDescription = "test1ShortDesc", longDescription = "test1LongDesc"), Movie(trackId = 2, movieName = "test2", shortDescription = "test2ShortDesc", longDescription = "test2LongDesc")))
                postsDao.upsertPosts(response ?: listOf())
                postsDao.upsertPosts(favMovList)
            }

            override suspend fun shouldFetchFromRemote(): Boolean {
                return true
            }

            override fun fetchFromLocal(): List<Post> {
                return postsDao.getAllPosts()
            }

            override suspend fun fetchFromRemote(): Response<List<Post>> {
                val mockedResponse = MockResponseFileReader("posts_response.json").content
                mockWebServer.enqueue(
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(mockedResponse)
                )
                return testApiService.getPosts()
            }

        }.asFlow().onCompletion {
            mockWebServer.shutdown()
        }
    }

    override suspend fun updatePosts(posts: List<Post>) {
        postsDao.upsertPosts(posts)
    }

    override suspend fun insertOrUpdateFavouritePost(favouritePost: FavouritePost) {
        favouritePostsDao.upsertFavouritePosts(listOf(favouritePost))
    }

    override suspend fun deleteFavouritePost(favouritePost: FavouritePost) {
        favouritePostsDao.deleteFavouritePosts(listOf(favouritePost))
    }

    override fun getFavouritePostsLocal(): Flow<Resource<List<FavouritePost>>> {
        return object : NetworkAndDbBoundRepository<List<FavouritePost>, List<FavouritePost>>() {
            override suspend fun saveRemoteData(response: List<FavouritePost>) {
            }

            override suspend fun shouldFetchFromRemote(): Boolean {
                return false
            }

            override fun fetchFromLocal(): List<FavouritePost> {
                return favouritePostsDao.getAllFavouritePosts()
            }

            override suspend fun fetchFromRemote(): Response<List<FavouritePost>>? {
                return null
            }

        }.asFlow()
    }
}
