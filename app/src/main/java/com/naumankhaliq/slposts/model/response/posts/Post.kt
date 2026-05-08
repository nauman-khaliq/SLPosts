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
import com.google.gson.annotations.SerializedName
import com.naumankhaliq.slposts.model.response.posts.Post.Companion.TABLE_NAME
import kotlinx.parcelize.Parcelize

@SuppressLint("ParcelCreator")
@Entity(tableName = TABLE_NAME)
@Parcelize
data class Post(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id") var id: Long? = null,
    @SerializedName("userId") var userId: Long? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("body") var body: String? = null,
    @SerializedName("artworkUrl30") var artWorkUrl130: String? = null,
    @SerializedName("artworkUrl60") var artWorkUrl160: String? = null,
    @SerializedName("artworkUrl100") var artWorkUrl1100: String? = null,
    @SerializedName("releaseDate") var releaseDate: String? = null,
    @SerializedName("trackPrice") var price: Float? = null,
    @SerializedName("currency") var currency: String? = null,
    @SerializedName("longDescription") var longDescription: String? = null,
    @SerializedName("primaryGenreName") var primaryGenre: String? = null,
    @SerializedName("previewUrl") var previewVideoUrl: String? = null,
    var isFavourite: Boolean = false,
): Parcelable {
    /**
     * Makes post favourite otherwise not
     */
    fun makeMovieFavOrNot() {
        isFavourite = !isFavourite
    }

    companion object {
        const val TABLE_NAME = "posts"
    }

}







