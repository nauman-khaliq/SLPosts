/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naumankhaliq.slposts.di

import com.naumankhaliq.slposts.data.repository.FakeSLPostsRepository
import com.naumankhaliq.slposts.data.repository.SLPostsRepository
import com.naumankhaliq.slposts.di.module.SLPostsRepositoryModule
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

/**
 * TasksRepository binding to use in tests.
 *
 * Hilt will inject a [FakeSLPostsRepository] instead of a [DefaultIMovieRepository].
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [SLPostsRepositoryModule::class]
)
abstract class TestTasksRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRepository(repo: FakeSLPostsRepository): SLPostsRepository
}
