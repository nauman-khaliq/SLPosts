/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 *
 */
package com.naumankhaliq.slposts.ui.home.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.naumankhaliq.slposts.databinding.FragmentSettingsBinding
import com.naumankhaliq.slposts.ui.auth.AuthActivity
import com.naumankhaliq.slposts.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

// Instances of this class are fragments representing a single
// object in our collection.

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SettingsFragment : BaseFragment<SettingsViewModel, FragmentSettingsBinding>() {
    override val mViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
    }

    override fun getViewBinding(): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(layoutInflater)
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initViews() {
        mViewBinding.run {
            logoutBtn.setOnClickListener {
                getLoggedOut()
            }

        }
    }

    /**
     * Makes logout request and gets logged out
     */
    private fun getLoggedOut() {
        mViewBinding.run {
            mViewModel.clearLogin()
            startActivity(Intent(requireActivity(), AuthActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

}