package com.likeminds.feedsx.feed.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemLikesScreenBinding
import com.likeminds.feedsx.feed.util.LikesScreenUtil
import com.likeminds.feedsx.feed.view.model.LikesViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_LIKES_SCREEN

class ItemLikesScreenViewDataBinder : ViewDataBinder<ItemLikesScreenBinding, LikesViewData>() {
    override val viewType: Int
        get() = ITEM_LIKES_SCREEN

    override fun createBinder(parent: ViewGroup): ItemLikesScreenBinding {
        return ItemLikesScreenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bindData(binding: ItemLikesScreenBinding, data: LikesViewData, position: Int) {
        LikesScreenUtil.initLikeItem(binding, data)
    }
}