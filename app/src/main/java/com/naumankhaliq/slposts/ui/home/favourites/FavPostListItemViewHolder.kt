/*
 *   MIT Licence
 *   Copyright (c) 2024 Nauman Khaliq.
 *
 */
package com.naumankhaliq.slposts.ui.home.favourites

import androidx.core.content.ContextCompat
import com.naumankhaliq.slposts.R
import com.naumankhaliq.slposts.ui.adapters.GenericAdapter
import com.naumankhaliq.slposts.databinding.ListItemPostBinding
import com.naumankhaliq.slposts.model.response.posts.Post

/**
 * View holder for Category Item button View
 */
class FavPostListItemViewHolder(private val binding: ListItemPostBinding, private val favMovieActionListener: FavMovieActionListener) :
    GenericAdapter.AbstractViewHolder<Post>(binding.root) {

    override fun bindItem(item: Post) {
        binding.titleTV.text = item.title
        binding.bodyTV.text = item.body
        updateFavUI()
        binding.favoriteIV.setOnClickListener {
            item.makeMovieFavOrNot()
            updateFavUI()
            favMovieActionListener.onFavBtnClicked(item)
        }
        itemView.setOnClickListener {
            clickListener?.onClicked(item)
        }
    }

    /**
     * Updates favourite btn ui
     */
    private fun updateFavUI() {
        if (item?.isFavourite == true) {
            binding.favoriteIV.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.ic_favorite_24))
        }
        else {
            binding.favoriteIV.setImageDrawable(ContextCompat.getDrawable(itemView.context,R.drawable.ic_favorite_border_24))
        }
    }

    /**
     * Interface for listener fav btn action listener
     */
    interface FavMovieActionListener {
        fun onFavBtnClicked(item: Post)
    }
}