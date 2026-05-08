/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.databinding.ActivityAuthBinding
import com.naumankhaliq.slposts.ui.base.BaseActivity
import com.naumankhaliq.slposts.ui.home.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Timer
import kotlin.concurrent.schedule

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AuthActivity : BaseActivity<AuthViewModel, ActivityAuthBinding>() {

    override val mViewModel: AuthViewModel by viewModels()
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
        if (mViewModel.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        initView()
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initView() {
        mViewBinding.run {

        }
    }

    override fun getViewBinding(): ActivityAuthBinding = ActivityAuthBinding.inflate(layoutInflater)

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    companion object {
    }
}
