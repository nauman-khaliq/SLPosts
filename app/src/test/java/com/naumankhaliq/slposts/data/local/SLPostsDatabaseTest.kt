package com.naumankhaliq.slposts.data.local

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.naumankhaliq.slposts.data.local.dao.PostsDao
import com.naumankhaliq.slposts.model.response.posts.Post
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP])
@OptIn(ExperimentalCoroutinesApi::class)
class SLPostsDatabaseTest: TestCase() {

    // get reference to the LanguageDatabase and LanguageDao class
    private lateinit var db: SLPostsDatabase
    private lateinit var movieDao: PostsDao

    // Override function setUp() and annotate it with @Before
    // this function will be called at first when this test class is called
    @Before
    public override fun setUp() {
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable
        db = Room.inMemoryDatabaseBuilder(context, SLPostsDatabase::class.java).build()
        movieDao = db.getPostsDao()
    }

    @After
    fun close() {
        db.close()
    }

    // create a test function and annotate it with @Test
    // here we are first adding an item to the db and then checking if that item
    // is present in the db -- if the item is present then our test cases pass
    @Test
    fun writeAndReadLanguage() = runBlocking (Dispatchers.IO){
        val post = Post(id = 123, title = "Test")
        movieDao.upsertPosts(listOf(post))
        val movies = movieDao.getAllPosts()
        assert(movies.get(0).id == post.id)
    }
}