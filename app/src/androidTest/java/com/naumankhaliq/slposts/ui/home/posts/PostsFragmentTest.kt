package com.naumankhaliq.slposts.ui.home.posts

import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.naumankhaliq.slposts.HiltTestActivity
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.data.repository.SLPostsRepository
import com.naumankhaliq.slposts.utils.clickChildViewWithId
import com.naumankhaliq.slposts.utils.launchFragmentInHiltContainer
import com.naumankhaliq.slposts.utils.nthChildOf
import com.naumankhaliq.slposts.utils.withImageViewDrawable
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers
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
class PostsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var slPostsRepository: SLPostsRepository

    val fragmentArgs = bundleOf("selectedListItem" to 0)
    lateinit var fragment: PostsFragment

    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        hiltRule.inject()
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        launchFragmentInHiltContainer<PostsFragment>(fragmentArgs) {
            fragment = this as PostsFragment
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.postsFragment)
            Navigation.setViewNavController(requireView(), navController)
            (fragment.activity as? HiltTestActivity)?.setNavigationController(navController)
        }
    }

    @Test
    fun testMoviesFragment() {
        assert(::fragment.isInitialized)
    }

    @Test
    fun clickingHeartBtnFromListShouldAddPostToFavourites() {
        // THEN - Verify task is displayed on screen
        Espresso.onView(ViewMatchers.withId(R.id.postsList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<PostListItemViewHolder>(0,
                clickChildViewWithId(R.id.favBtn)
            ))

        // Check if the ImageView's color turns red after clicking
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.favBtn),
                ViewMatchers.isDescendantOfA(ViewMatchers.withId(R.id.postsList).nthChildOf(0))
            )
        )
            .check(
                ViewAssertions.matches(
                    withImageViewDrawable(
                        ContextCompat.getDrawable(
                            fragment.requireContext(),
                            R.drawable.ic_favorite_24
                        )
                    )
                )
            )
    }




    @After
    fun tearDown() {
    }

}