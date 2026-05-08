/*
 * MIT License
 *
 * Copyright (c) 2024 Nauman Khaliq
 *
 */
package com.naumankhaliq.slposts.ui.home.favourites

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.ui.adapters.GenericAdapter
import com.naumankhaliq.slposts.data.repository.DataFrom
import com.naumankhaliq.slposts.databinding.FragmentFavPostsBinding
import com.naumankhaliq.slposts.databinding.ListItemPostBinding
import com.naumankhaliq.slposts.model.State
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.ui.base.BaseFragment
import com.naumankhaliq.slposts.ui.home.MainActivity
import com.naumankhaliq.slposts.ui.home.MainViewModel
import com.naumankhaliq.slposts.utils.PreferenceHelper
import com.naumankhaliq.slposts.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

// Instances of this class are fragments representing a single
// object in our collection.

@ExperimentalCoroutinesApi
@AndroidEntryPoint
open class FavouritePostsFragment : BaseFragment<MainViewModel, FragmentFavPostsBinding>(), FavPostListItemViewHolder.FavMovieActionListener {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    override val mViewModel: MainViewModel by activityViewModels()

    private var moviesListAdapter: GenericAdapter<Post> = GenericAdapter(
        { view -> FavPostListItemViewHolder(ListItemPostBinding.bind(view), this) },
        object : GenericAdapter.ClickListener<Post> {
            override fun onClicked(item: Post) {
            }
        },
        R.layout.list_item_post,
        null,
        object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mViewBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Gets fav posts through viewmodel from api or local storage
     */
    protected open fun getFavPosts() {
        mViewModel.getFavouritePosts()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getFavPosts()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        observeFavMovies()
    }

    override fun getViewBinding(): FragmentFavPostsBinding {
        return FragmentFavPostsBinding.inflate(layoutInflater)
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initViews() {
        mViewBinding.run {
            postsList.setHasFixedSize(true)
            postsList.adapter = moviesListAdapter
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                getFavPosts()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }


    /**
     * Observing users list State Flow data with states
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun observeFavMovies() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.favPosts.collect {
                    when (it) {
                        is State.Loading -> {
                            Log.i("LoadingStateChecking", "Users list Loading Called")
                            mViewBinding.swipeRefreshLayout.isRefreshing = true
                        }
                        is State.Success -> {
                            Log.i("LoadingStateChecking", "Users list Success Called")
                            it.data.let { movies ->
                                if (it.dataFrom == DataFrom.CACHED) {
                                    moviesListAdapter.items = if (movies.isNotEmpty()) ArrayList(movies) else arrayListOf()
                                    moviesListAdapter.notifyDataSetChanged()
                                    updateNoDataUI()
                                }
                            }
                            mViewBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        is State.Error -> {
                            Log.i("LoadingStateChecking", "Users list ERROR Called")
                            val obj = it.message
                            Toast.makeText(context, obj, Toast.LENGTH_SHORT).show()
                            mViewBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    /**
     * Updates appropriate ui of data is empty or not
     */
    private fun updateNoDataUI() {
        if (moviesListAdapter.items.isEmpty()) {
            mViewBinding.noDataTV.visibility = View.VISIBLE
        }
        else {
            mViewBinding.noDataTV.visibility = View.GONE
        }
    }

    override fun onFavBtnClicked(item: Post) {
        if (!item.isFavourite) {
            mViewModel.removePostFromFavourites(item)
            moviesListAdapter.notifyItemRemoved(moviesListAdapter.items.indexOf(item))
            moviesListAdapter.items.remove(item)
            updateNoDataUI()
            mViewBinding.root.showSnackBar(getString(R.string.removed_from_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
    }
}