package com.naumankhaliq.slposts.ui.main

import android.os.Build
import com.naumankhaliq.slposts.data.local.SLPostsDatabase
import com.naumankhaliq.slposts.data.local.dao.FavouritePostsDao
import com.naumankhaliq.slposts.data.local.dao.PostsDao
import com.naumankhaliq.slposts.data.remote.api.SLPostsService
import com.naumankhaliq.slposts.data.repository.DataFrom
import com.naumankhaliq.slposts.data.repository.DefaultSLPostsRepository
import com.naumankhaliq.slposts.data.repository.Resource
import com.naumankhaliq.slposts.model.State
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.ui.home.MainViewModel
import com.naumankhaliq.slposts.utils.PreferenceHelper
import com.naumankhaliq.slposts.utils.TestRetrofitHelper
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.LOLLIPOP], application = HiltTestApplication::class)
class MainViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val mockkRule = MockKRule(this)

    @MockK
    private lateinit var mainViewModelT: MainViewModel

    @MockK
    lateinit var testApiService: SLPostsService

    @MockK
    lateinit var fakeRepository: FakeSLPostsRepository

    @MockK
    @Inject
    lateinit var preferenceHelper: PreferenceHelper

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


    @Before
    fun setUp() {
        hiltRule.inject()
        MockKAnnotations.init()
        mockWebServer = MockWebServer()
        mockWebServer.start()
        testApiService =
            TestRetrofitHelper.getTestRetrofitServiceForMockServer<SLPostsService>(mockWebServer)
        fakeRepository = FakeSLPostsRepository(postsDao, favouritePostsDao, testApiService)
        mainViewModelT = MainViewModel(fakeRepository, preferenceHelper)
    }

    @After
    fun tearDown() {
        slPostsDB.close()
        mockWebServer.close()
    }

    @Test
    fun test_state_flows_are_in_idle_state() {
        assert(mainViewModelT.posts.value is State.Idle)
        assert(mainViewModelT.favPosts.value is State.Idle)
    }

    @Test
    fun test_posts_state_flow_is_behaving_correctly() = runTest(UnconfinedTestDispatcher()) {
        mainViewModelT.getPosts()
        mainViewModelT.posts.value.let { state ->
            when (state) {
                is State.Loading -> {
                    assert(state.isLoading() == true)
                }
                else -> {}
            }
        }
        fakeRepository.emitMovieFlow(Resource.Success(listOf(Post()), DataFrom.REMOTE))
        mainViewModelT.posts.value.let { state ->
            when (state) {
                is State.Success -> {
                    assert(state.data.size == 1)
                }
                else -> {}
            }
        }
    }

    @Test
    fun test_favposts_state_flow_is_behaving_correctly() = runTest(UnconfinedTestDispatcher()) {
        mainViewModelT.getFavouritePosts()
        mainViewModelT.favPosts.value.let { state ->
            when (state) {
                is State.Loading -> {
                    assert(state.isLoading() == true)
                }
                else -> {}
            }
        }
        fakeRepository.emitFavMovieFlow(Resource.Success(listOf(FavouritePost(1L, Post(title = "test"))), DataFrom.REMOTE))
        mainViewModelT.favPosts.value.let { state ->
            when (state) {
                is State.Success -> {
                    assert(state.data.get(0).title == "test")
                }
                else -> {}
            }
        }
    }

    class FakeSLPostsRepository(
        postsDao: PostsDao,
        favouritePostsDao: FavouritePostsDao,
        apiService: SLPostsService
    ) : DefaultSLPostsRepository(postsDao, favouritePostsDao, apiService) {
        private val postFlow = MutableSharedFlow<Resource<List<Post>>>()
        private val favMovieFlow = MutableSharedFlow<Resource<List<FavouritePost>>>()
        suspend fun emitMovieFlow(value: Resource<List<Post>>) = postFlow.emit(value)
        suspend fun emitFavMovieFlow(value: Resource<List<FavouritePost>>) = favMovieFlow.emit(value)
        override fun getPosts(): Flow<Resource<List<Post>>> {
            return postFlow
        }
        override fun getFavouritePostsLocal(): Flow<Resource<List<FavouritePost>>> {
            return favMovieFlow
        }
    }

}