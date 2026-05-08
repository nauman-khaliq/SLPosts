package com.naumankhaliq.slposts.ui.home.posts

import androidx.core.content.ContextCompat
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.databinding.ListItemPostBinding
import com.naumankhaliq.slposts.model.response.posts.Post
import com.naumankhaliq.slposts.ui.adapters.GenericAdapter

/**
 * View holder for Category Item button View
 */
class PostListItemViewHolder(private val binding: ListItemPostBinding, private val postActionListener: PostActionListener) :
    GenericAdapter.AbstractViewHolder<Post>(binding.root) {

    override fun bindItem(item: Post) {

        binding.titleTV.text = item.title
        binding.bodyTV.text = item.body
        updateFavUI()
        binding.favoriteIV.setOnClickListener {
            item.makeMovieFavOrNot()
            updateFavUI()
            postActionListener.onFavBtnClicked(item)
        }
        itemView.setOnClickListener {
            item.makeMovieFavOrNot()
            updateFavUI()
            postActionListener.onFavBtnClicked(item)
            clickListener?.onClicked(item)
        }
    }

    /**
     * Updates favourite btn ui
     */
    private fun updateFavUI() {
        if (item?.isFavourite == true) {
            binding.favoriteIV.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_24))
        }
        else {
            binding.favoriteIV.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_favorite_border_24))
        }
    }

    /**
     * Interface for listener fav btn action listener
     */
    interface PostActionListener {
        fun onFavBtnClicked(item: Post)
    }
}