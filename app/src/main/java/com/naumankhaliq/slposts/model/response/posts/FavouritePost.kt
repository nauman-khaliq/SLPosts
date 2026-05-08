/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */
package com.naumankhaliq.slposts.model.response.posts

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.naumankhaliq.slposts.model.response.posts.FavouritePost.Companion.TABLE_NAME
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Entity(tableName = TABLE_NAME)
@Parcelize
data class FavouritePost(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    val post: Post
): Parcelable {
    companion object {
        const val TABLE_NAME = "fav_posts"
    }
}




/**
 * [MovieJsonConverter] will be used to convert movie to json and back for room database storage
 */
class MovieJsonConverter {

    private val gson = Gson()

    /**
     * Converts movie of type [Post] to json [String]
     */
    @TypeConverter
    fun fromMovie(post: Post?): String? {
        return gson.toJson(post)
    }

    /**
     * Converts json [String] to [Post]
     */
    @TypeConverter
    fun toMovie(movieJson: String?): Post? {
        return runCatching {
            gson.fromJson(movieJson, Post::class.java)
        }.getOrNull()
    }
}







