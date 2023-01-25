package com.likeminds.feedsx.overflowmenu.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsx.databinding.ItemOverflowMenuBinding
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_OVERFLOW_MENU_ITEM

class OverflowMenuItemBinder constructor(
    val overflowMenuAdapterListener: OverflowMenuAdapterListener
) : ViewDataBinder<ItemOverflowMenuBinding, BaseViewType>() {

    override val viewType: Int
        get() = ITEM_OVERFLOW_MENU_ITEM

    override fun createBinder(parent: ViewGroup): ItemOverflowMenuBinding {
        val binding =
            ItemOverflowMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener {
            val data = binding.data ?: return@setOnClickListener
            overflowMenuAdapterListener.onMenuItemClicked(data)
        }
        return binding
    }

    override fun bindData(
        binding: ItemOverflowMenuBinding,
        data: BaseViewType,
        position: Int
    ) {
        binding.data = data as OverflowMenuItemViewData
    }
}