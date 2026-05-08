/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */

package com.naumankhaliq.slposts.di.module

import com.naumankhaliq.slposts.data.repository.SLPostsRepository
import com.naumankhaliq.slposts.data.repository.DefaultSLPostsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Currently SLPostsRepository is only used in ViewModels.
 * MainViewModel is not injected using @HiltViewModel so can't install in ViewModelComponent.
 */
@ExperimentalCoroutinesApi
@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class SLPostsRepositoryModule {

    /**
     * Binds DefaultSLPostsRepository returns [SLPostsRepository] which is an interface and parent of [DefaultSLPostsRepository]
     * @param repository of type [DefaultSLPostsRepository]
     * @return [SLPostsRepository]
     */
    @ActivityRetainedScoped
    @Binds
    abstract fun bindSLPostsRepository(repository: DefaultSLPostsRepository): SLPostsRepository
}
