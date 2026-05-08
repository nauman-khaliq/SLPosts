/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.ui.auth

import androidx.lifecycle.ViewModel
import com.naumankhaliq.slposts.data.repository.DataFrom
import com.naumankhaliq.slposts.data.repository.SLPostsRepository
import com.naumankhaliq.slposts.model.State
import com.naumankhaliq.slposts.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel for [LoginFragment]
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val slPostsRepository: SLPostsRepository,
    private val preferenceHelper: PreferenceHelper
) :
    ViewModel() {

    private val _login: MutableStateFlow<State<Boolean>> = MutableStateFlow(State.idle())
    val login: StateFlow<State<Boolean>> = _login

    fun postLogin(email: String, password: String) {
        preferenceHelper.saveAccessToken("accessToken")
        _login.value = State.success( data = true, dataFrom = DataFrom.REMOTE)
    }

    fun isLoggedIn(): Boolean {
        return preferenceHelper.getAccessToken()?.isNotEmpty() ?: false
    }

}


