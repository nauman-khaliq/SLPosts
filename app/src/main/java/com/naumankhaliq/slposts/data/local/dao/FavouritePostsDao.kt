/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [com.naumankhaliq.slposts.data.local.dao.FavouritePostsDao]
 */
@Dao
interface FavouritePostsDao {

    /**
     * Inserts [FavouritePost] into the [FavouritePost.TABLE_NAME] Match.
     * Duplicate values are replaced in the post.
     * @param favMovies pass [List] of [FavouritePost]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavouritePosts(favMovies: List<FavouritePost>)


    /**
     * Inserts if not exist or updates if it exists [FavouritePost] into the [FavouritePost.TABLE_NAME] on the basis of primary key.
     * @param favMovies pass [List] of [FavouritePost]
     */
    @Upsert
    suspend fun upsertFavouritePosts(favMovies: List<FavouritePost>)

    /**
     * Deletes all the fav movies from the [FavouritePost.TABLE_NAME] Match.
     */
    @Query("DELETE FROM ${FavouritePost.TABLE_NAME}")
    suspend fun deleteAllFavouritePosts()


    /**
     * Fetches the post from the [FavouritePost.TABLE_NAME] whose primaryKeyId is [primaryKeyId].
     * @param primaryKeyId Unique ID of [FavouritePost]
     * @return [Flow] of [FavouritePost] from database.
     */
    @Query("SELECT * FROM ${FavouritePost.TABLE_NAME} WHERE id = :trackId")
    fun getFavouritePostById(trackId: String): Flow<FavouritePost>


    /**
     * Fetches all the fav posts from the [FavouritePost.TABLE_NAME] FavouriteMovie.
     * @return [List] of [FavouritePost]
     */
    @Query("SELECT * FROM ${FavouritePost.TABLE_NAME}")
    fun getAllFavouritePosts(): List<FavouritePost>

    /**
     * Fetches count for all the fav posts from the [FavouritePost.TABLE_NAME] Match.
     * @return [List] of [FavouritePost]
     */
    @Query("SELECT COUNT(id) FROM ${FavouritePost.TABLE_NAME}")
    fun getFavouritePostsCount(): Int

    /**
     * Delete [FavouritePost] from the [FavouritePost.TABLE_NAME] .
     * @param favPosts pass [List] of [FavouritePost]
     */
    @Delete
    suspend fun deleteFavouritePosts(favPosts: List<FavouritePost>)
}
