/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 *
 */
package com.naumankhaliq.slposts.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naumankhaliq.slposts.databinding.FragmentLoginBinding
import com.naumankhaliq.slposts.model.State
import com.naumankhaliq.slposts.ui.base.BaseFragment
import com.naumankhaliq.slposts.ui.home.MainActivity
import com.naumankhaliq.slposts.utils.extensions.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Instances of this class are fragments representing a single
// object in our collection.

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding>() {
    override val mViewModel: AuthViewModel by activityViewModels()

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

    override fun getViewBinding(): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(layoutInflater)
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initViews() {
        mViewBinding.run {
            signInButton.setOnClickListener {
                getLoggedIn()
            }
            userOrEmailET.doAfterTextChanged {
                validateFields()
            }
            passwordET.doAfterTextChanged {
                validateFields()
            }
            userOrEmailET.requestFocus()

        }
        observeLogin()
    }

    /**
     * Validate data and update sign in button
     */
    private fun validateFields() {
        mViewBinding.run {
            signInButton.isEnabled = userOrEmailET.text?.toString()?.isValidEmail() == true  && (passwordET.text?.toString()?.length in 8..15)
        }
    }

    /**
     * Makes login request and gets logged in
     */
    private fun getLoggedIn() {
        mViewBinding.run {
            val email = userOrEmailET.text?.toString() ?: ""
            val password = passwordET.text?.toString() ?: ""
            mViewModel.postLogin(email, password)
        }
    }

    /**
     * Observing login state
     */
     private fun observeLogin() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.login.collectLatest {
                    when (it) {
                        is State.Loading -> {
                            mViewBinding.signInButton.isEnabled = false
                        }
                        is State.Success -> {
                            if (it.data) {
                                requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                        is State.Error -> {
                            mViewBinding.signInButton.isEnabled = true
                        }
                        is State.Idle -> {

                        }
                    }
                }
            }
        }
    }

}