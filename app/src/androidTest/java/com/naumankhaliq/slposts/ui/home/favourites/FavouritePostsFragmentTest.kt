package com.naumankhaliq.slposts.ui.home.favourites

import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naumankhaliq.slposts.HiltTestActivity
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.data.repository.SLPostsRepository
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.ui.home.favourites.FavouritePostsFragment
import com.naumankhaliq.slposts.utils.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class FavouritePostsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    lateinit var fragment: FavouritePostsFragment

    @Inject
    lateinit var slPostsRepository: SLPostsRepository

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<FavouritePostsFragment> {
            fragment = this as FavouritePostsFragment
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.favouritePostsFragment)
            Navigation.setViewNavController(requireView(), navController)
            (fragment.activity as? HiltTestActivity)?.setNavigationController(navController)
            runBlocking {
                slPostsRepository.insertOrUpdateFavouritePost(FavouritePost(1, Post(1, title = "Fav Movie 1", primaryGenre = "test", body = "fav movie 1 test")))
                slPostsRepository.insertOrUpdateFavouritePost(FavouritePost(2, Post(2, title = "Fav Movie 2", primaryGenre = "test", body = "fav movie 2 test")))
            }
        }
    }

    @Test
    fun testFavouritePostsFragment() {
        assert(::fragment.isInitialized)
    }

    @Test
    fun testsFavouritesPostsAreShown() {
        Thread.sleep(2000)
        // THEN - Verify task is displayed on screen
        onView(withId(R.id.logo)).check(matches(withText("Favourites")))
        // THEN - Verify task is displayed on screen
        onView(withText("Fav Movie 1")).check(matches(isDisplayed()))
        onView(withText("Fav Movie 2")).check(matches(isDisplayed()))

    }


    @After
    fun tearDown() {
    }

}