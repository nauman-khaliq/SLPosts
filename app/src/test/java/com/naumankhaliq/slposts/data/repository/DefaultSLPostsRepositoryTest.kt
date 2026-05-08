package com.naumankhaliq.slposts.data.repository

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.naumankhaliq.slposts.data.local.SLPostsDatabase
import com.naumankhaliq.slposts.data.local.dao.FavouritePostsDao
import com.naumankhaliq.slposts.data.local.dao.PostsDao
import com.naumankhaliq.slposts.data.remote.api.SLPostsService
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.utils.MockResponseFileReader
import com.naumankhaliq.slposts.utils.TestRetrofitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP], application = HiltTestApplication::class)
class DefaultSLPostsRepositoryTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    private lateinit var repository: DefaultSLPostsRepository

    @MockK
    lateinit var testApiService: SLPostsService

    @MockK
    @Inject
    lateinit var slPostsDB: SLPostsDatabase

    @MockK
    @Inject
    lateinit var postsDao: PostsDao

    @MockK
    @Inject
    lateinit var favouritePostsDao: FavouritePostsDao

    private lateinit var mockWebServer: MockWebServer

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        hiltRule.inject()
        MockKAnnotations.init()
        // get context -- since this is an instrumental test it requires
        // context from the running application
        val context = ApplicationProvider.getApplicationContext<Context>()
        // initialize the db and dao variable

        mockWebServer = MockWebServer()
        mockWebServer.start()
        testApiService = TestRetrofitHelper.getTestRetrofitServiceForMockServer<SLPostsService>(mockWebServer)
        repository = DefaultSLPostsRepository(postsDao, favouritePostsDao, testApiService)
    }

    @After
    fun tearDown() {
        slPostsDB.close()
        mockWebServer.shutdown()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun tests_getPosts_is_getting_and_storing_response_correctly_first_time() = runTest(UnconfinedTestDispatcher()) {
        val mockedResponse = MockResponseFileReader("posts_response.json").content
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
            val res = repository.getPosts().toList()

        val firstEmission = res[0]
        val secondEmission = res[1]
            when (firstEmission) {
                is Resource.Success -> {
                    assert(firstEmission.data.isEmpty())
                }

                is Resource.Failed -> {
                    assert(firstEmission.message.isEmpty())
                }
            }

        when (secondEmission) {
            is Resource.Success -> {
                assert(secondEmission.data.isNotEmpty())
            }

            is Resource.Failed -> {
                assert(secondEmission.message.isNotEmpty())
            }
        }
        var isMovieStoredToDB = false
        isMovieStoredToDB = postsDao.getAllPosts().isNotEmpty()
        assert(isMovieStoredToDB)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun tests_getFavouritePostsLocal_is_getting_response_correctly_first_time() = runTest(UnconfinedTestDispatcher()) {

        favouritePostsDao.upsertFavouritePosts(listOf(FavouritePost(1L, Post(id = 1L))))
        val res = repository.getFavouritePostsLocal().toList()
        val firstEmission = res[0]
        when (firstEmission) {
            is Resource.Success -> {
                assert(firstEmission.data.get(0).id == 1L)
            }

            is Resource.Failed -> {
                assert(firstEmission.message.isEmpty())
            }
        }

        var isFavMovieStoredToDB = false
        isFavMovieStoredToDB = favouritePostsDao.getAllFavouritePosts().isNotEmpty()
        assert(isFavMovieStoredToDB)
    }
}