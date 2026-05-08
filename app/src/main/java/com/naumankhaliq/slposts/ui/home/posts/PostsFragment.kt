package com.naumankhaliq.slposts.ui.home.posts

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.data.repository.DataFrom
import com.naumankhaliq.slposts.databinding.FragmentHomeBinding
import com.naumankhaliq.slposts.databinding.ListItemPostBinding
import com.naumankhaliq.slposts.model.State
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.ui.adapters.GenericAdapter
import com.naumankhaliq.slposts.ui.base.BaseFragment
import com.naumankhaliq.slposts.ui.home.MainActivity
import com.naumankhaliq.slposts.ui.home.MainViewModel
import com.naumankhaliq.slposts.utils.PreferenceHelper
import com.naumankhaliq.slposts.utils.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
open class PostsFragment : BaseFragment<MainViewModel, FragmentHomeBinding>(), PostListItemViewHolder.PostActionListener {
    @Inject
    lateinit var preferenceHelper: PreferenceHelper
    override val mViewModel: MainViewModel by activityViewModels()

    private val postsListAdapter: GenericAdapter<Post> = GenericAdapter(
        { view -> PostListItemViewHolder(ListItemPostBinding.bind(view), this) },
        object : GenericAdapter.ClickListener<Post> {
            override fun onClicked(item: Post) {
                onPostItemClicked(item)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    /**
     * Gets posts through viewmodel from api or local storage
     */
    protected open fun getPosts() {
        mViewModel.getPosts()
    }

    protected open fun onPostItemClicked(item: Post) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getPosts()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        observePosts()
    }

    override fun getViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    /**
     * Initializing views and register click listeners
     */
    private fun initViews() {
        mViewBinding.run {
            postsList.setHasFixedSize(true)
            postsList.adapter = postsListAdapter
            swipeRefreshLayout.setOnRefreshListener {
                swipeRefreshLayout.isRefreshing = true
                getPosts()
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }


    /**
     * Observing users list State Flow data with states
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun observePosts() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.posts.collect {
                    when (it) {
                        is State.Loading -> {
                            mViewBinding.swipeRefreshLayout.isRefreshing = true
                        }
                        is State.Success -> {
                            it.data.let { posts ->
                                postsListAdapter.items = ArrayList(posts)
                                postsListAdapter.notifyDataSetChanged()
                                if (it.dataFrom == DataFrom.REMOTE) {
                                    mViewBinding.swipeRefreshLayout.isRefreshing = false
                                }
                            }
                        }
                        is State.Error -> {
                            val obj = it.message
                            mViewBinding.swipeRefreshLayout.isRefreshing = false
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    override fun onFavBtnClicked(item: Post) {
        if (item.isFavourite) {
            mViewModel.addPostToFavourites(item)
            mViewBinding.root.showSnackBar(getString(R.string.added_to_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
        else {
            mViewModel.removePostFromFavourites(item)
            mViewBinding.root.showSnackBar(getString(R.string.removed_from_favourites_mess), anchorView = (requireActivity() as? MainActivity)?.getBottomNavView())
        }
    }

}