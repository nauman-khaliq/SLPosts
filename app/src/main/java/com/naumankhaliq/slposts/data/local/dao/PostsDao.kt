/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.naumankhaliq.slposts.model.response.posts.Post
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [com.naumankhaliq.slposts.data.local.dao.PostsDao]
 */
@Dao
interface PostsDao {

    /**
     * Inserts [Post] into the [Post.TABLE_NAME] Match.
     * Duplicate values are replaced in the Match.
     * @param posts pass [List] of [Post]
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPosts(posts: List<Post>)


    /**
     * Inserts if not exist or updates if it exists [Post] into the [Post.TABLE_NAME] on the basis of primary key.
     * @param posts pass [List] of [Post]
     */
    @Upsert
    suspend fun upsertPosts(posts: List<Post>)

    /**
     * Deletes all the posts from the [Post.TABLE_NAME].
     */
    @Query("DELETE FROM ${Post.TABLE_NAME}")
    suspend fun deleteAllPosts()


    /**
     * Fetches the post from the [Post.TABLE_NAME] Match whose primaryKeyId is [postId].
     * @param postId Unique ID of [Post]
     * @return [Flow] of [Post] from database.
     */
    @Query("SELECT * FROM ${Post.TABLE_NAME} WHERE id = :postId")
    fun getPostById(postId: String): Flow<Post>

    /**
     * Fetches the post from the [Post.TABLE_NAME] Match whose primaryKeyId is [name].
     * @param name Unique ID of [Post]
     * @return [Flow] of [Post] from database Match.
     */
    @Query("SELECT * FROM ${Post.TABLE_NAME} WHERE title is :name")
    fun getPostByName(name: String): Post?

    /**
     * Fetches all the posts from the [Post.TABLE_NAME] Movie.
     * @return [List] of [Post]
     */
    @Query("SELECT * FROM ${Post.TABLE_NAME}")
    fun getAllPosts(): List<Post>

    /**
     * Fetches count for all the posts from the [Post.TABLE_NAME].
     * @return [List] of [Post]
     */
    @Query("SELECT COUNT(id) FROM ${Post.TABLE_NAME}")
    fun getPostsCount(): Int
}
