package com.naumankhaliq.slposts.ui.home

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.databinding.ActivityMainBinding
import com.naumankhaliq.slposts.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Timer
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val mViewModel: MainViewModel by viewModels()
    var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar) // Set AppTheme before setting content view.
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

        Timer().schedule(1000) {
            ready = true
        }
        // Set up an OnPreDrawListener to the root view.
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Check if the initial data is ready.
                    return if (ready) {
                        // The content is ready; start drawing.
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else {
                        // The content is not ready; suspend.
                        false
                    }
                }
            }
        )

//      It's very important to set the toolbar to prevent errors of the NPE type,
//      this is because the application style is .NoActionBar
        setSupportActionBar(mViewBinding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.postsFragment)
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(mViewBinding.bottomNavigationView, navController)
        initView()
        navController.currentDestination?.let { shouldShowNavigationView(it) }
        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?,
            ) {
                shouldShowNavigationView(destination)
            }
        })

    }

    /**
     * Initializing views and register click listeners
     */
    private fun initView() {
        mViewBinding.run {

        }
    }

    /**
     * Decides whether to show bottom navigation view based on current destination
     */
    private fun shouldShowNavigationView(currentDestination: NavDestination) {
        if (currentDestination.id == R.id.postsFragment || currentDestination.id == R.id.favouritePostsFragment || currentDestination.id == R.id.settingsFragment) {
            mViewBinding.bottomNavigationView.visibility = View.VISIBLE
            mViewBinding.appBarLayout.visibility = View.VISIBLE
        }
        else {
            mViewBinding.bottomNavigationView.visibility = View.GONE
            mViewBinding.appBarLayout.visibility = View.GONE
        }
    }

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    /**
     * Returns bottom navigation view
     */
    fun getBottomNavView(): BottomNavigationView {
        return mViewBinding.bottomNavigationView
    }
    companion object {
        const val test = "Final\nMar 18 - 7:00 PM\nLahore"
    }
}