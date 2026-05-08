/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.ui.home.settings

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
class SettingsViewModel @Inject constructor(
    private val slPostsRepository: SLPostsRepository,
    private val preferenceHelper: PreferenceHelper
) :
    ViewModel() {


    fun clearLogin() {
        preferenceHelper.deleteAccessToken()
    }


}


