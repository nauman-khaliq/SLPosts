package com.naumankhaliq.slposts.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naumankhaliq.slposts.data.repository.DataFrom
import com.naumankhaliq.slposts.data.repository.SLPostsRepository
import com.naumankhaliq.slposts.model.State
import com.naumankhaliq.slposts.model.response.posts.FavouritePost
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.utils.PreferenceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for [MainActivity]
 */
@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val slPostsRepository: SLPostsRepository,
    private val preferenceHelper: PreferenceHelper
) :
    ViewModel() {

    private val _posts: MutableStateFlow<State<List<Post>>> =
        MutableStateFlow(State.Companion.idle())
    val posts: StateFlow<State<List<Post>>> = _posts

    val _favPosts: MutableStateFlow<State<List<Post>>> = MutableStateFlow(State.Companion.idle())
    val favPosts: StateFlow<State<List<Post>>> = _favPosts
    /**
     * Gets posts list using [SLPostsRepository] mapping on [State] and passing data to _orders [StateFlow]
     */
    fun getPosts() {
        _posts.value = State.Companion.loading()
        viewModelScope.launch {
            slPostsRepository.getPosts()
                .map {
                    State.Companion.fromResource(it)
                }
                .collectLatest {
                    _posts.value = it
                }
        }
    }

    /**
     * Gets favourite posts list using [SLPostsRepository] mapping on [State] and passing data to [StateFlow]
     */
    fun getFavouritePosts() {
        _favPosts.value = State.Companion.loading()
        viewModelScope.launch {
            slPostsRepository.getFavouritePostsLocal()
                .map {
                    State.Companion.fromResource(it)
                }
                .collectLatest {
                    _favPosts.value = generatePostsListFromFav(it)
                }
        }
    }

    /**
     * Generates posts list from favourite posts model
     */
    private fun generatePostsListFromFav(state: State<List<FavouritePost>>): State<List<Post>> {
        when (state) {
            is State.Success -> {
                val arrayList = ArrayList<Post>()
                state.data.forEach {
                    arrayList.add(it.post)
                }
                return State.Success(arrayList.toList(), DataFrom.CACHED)
            }

            is State.Error -> {
                return State.Error(state.message)
            }

            else -> {

            }
        }
        return State.Error("Something went wrong")
    }

    /**
     * Add or update post to favourites
     * @param post pass [Post]
     */
    fun addPostToFavourites(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            post.id?.let {
                slPostsRepository.insertOrUpdateFavouritePost(FavouritePost(it, post))
                slPostsRepository.updatePosts(listOf(post))
            }
        }
    }

    /**
     * Delete post from favourites
     * @param post pass [Post]
     */
    fun removePostFromFavourites(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            post.id?.let {
                slPostsRepository.deleteFavouritePost(FavouritePost(it, post))
            }
        }
    }

}