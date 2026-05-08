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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

interface SLPostsRepository {
    /**
     * Fetches posts list from itunes api
     * @return Flow<Resource<List<Post>>>
     */
    fun getPosts(): Flow<Resource<List<Post>>>

    /**
     * Updates posts list from api
     * @param posts pass [List] of [Post]
     */
    suspend fun updatePosts(posts: List<Post>)

    /**
     * Updates inserts or updates favourite post
     * @param favouritePost pass which post wants to update in room
     */
    suspend fun insertOrUpdateFavouritePost(favouritePost: FavouritePost)

    /**
     * Deletes Favourite post
     * @param favouritePost pass which post wants to delete in room
     */
    suspend fun deleteFavouritePost(favouritePost: FavouritePost)

    /**
     * Fetches favourite posts list from local db
     * @return Flow<Resource<List<FavouritePost>>>
     */
    fun getFavouritePostsLocal(): Flow<Resource<List<FavouritePost>>>
}

/**
 * Singleton repository for fetching data from remote and storing it in database
 * for offline capability. This is Single source of data.
 */
@ExperimentalCoroutinesApi
open class DefaultSLPostsRepository @Inject constructor(
    private val postsDao: PostsDao,
    private val favouritePostsDao: FavouritePostsDao,
    val slPostsService: SLPostsService
) : SLPostsRepository {

    override fun getPosts(): Flow<Resource<List<Post>>> {
        return object: NetworkAndDbBoundRepository<List<Post>, List<Post>>() {
            override suspend fun saveRemoteData(response: List<Post>) {
                val favMovies = favouritePostsDao.getAllFavouritePosts()
                val favMovList = ArrayList<Post>()
                favMovies.forEach {
                    favMovList.add(it.post)
                }
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
                return slPostsService.getPosts()
            }

        }.asFlow()
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
