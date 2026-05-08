/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 */

package com.naumankhaliq.slposts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.naumankhaliq.slposts.data.local.dao.FavouritePostsDao
import com.naumankhaliq.slposts.data.local.dao.PostsDao
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.model.response.posts.MovieJsonConverter

/**
 * Abstract I Movies database.
 * It provides DAO [PostsDao] by using method [getPostsDao].
 */
@Database(
    entities = [Post::class, FavouritePost::class],
    version = DatabaseMigrations.DB_VERSION
)
@TypeConverters(MovieJsonConverter::class)
abstract class SLPostsDatabase : RoomDatabase() {

    /**
     * @return [PostsDao] User Data Access Object.
     */
    abstract fun getPostsDao(): PostsDao

    /**
     * @return [FavouritePostsDao] User Data Access Object.
     */
    abstract fun getFavouritePostsDao(): FavouritePostsDao

    companion object {
        const val DB_NAME = "slposts_database"

        @Volatile
        private var INSTANCE: SLPostsDatabase? = null

        /**
         * @return database[SLPostsDatabase] instance.
         */
        fun getInstance(context: Context): SLPostsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SLPostsDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
