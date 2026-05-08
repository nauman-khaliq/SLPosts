package com.naumankhaliq.slposts.ui.home

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.naumankhaliq.slposts.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

//    @get:Rule
//    val mockkRule = MockKRule(this)
@get:Rule
var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

    lateinit var activity: MainActivity

    @Before
    fun setUp() {
        val scenario = activityScenarioRule.scenario
        scenario.onActivity { act ->
            activity = act
        }
    }

    @Test
    fun testActivityUsingActivityRule() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            activityScenarioRule.scenario.moveToState(Lifecycle.State.CREATED)
        }.invokeOnCompletion {
            assert(activityScenarioRule.scenario.state == Lifecycle.State.CREATED)
            assert(::activity.isInitialized)
        }
    }

    @Test
    fun testActivity() {
        launchActivity<MainActivity>().use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.onActivity { activity ->
                assert(activity != null)
            }
        }
    }

    @Test
    fun testNavigationToInPostsScreen() {
        // Create a TestNavHostController
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )

        launchActivity<MainActivity>().use { scenario ->
            scenario.moveToState(Lifecycle.State.CREATED)
            scenario.onActivity { activity ->
                // Set the graph on the TestNavHostController
                navController.setGraph(R.navigation.nav_graph)
                // Make the NavController available via the findNavController() APIs
                Navigation.setViewNavController(activity.getViewBinding().root , navController)
                assert(navController.currentDestination?.id == R.id.postsFragment)
            }
        }

    }

    @After
    fun tearDown() {
        activityScenarioRule.scenario.close()
    }
}