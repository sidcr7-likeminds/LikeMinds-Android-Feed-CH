package com.likeminds.feedsample.delete.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.likeminds.feedsample.databinding.ItemReasonChooseBinding
import com.likeminds.feedsample.delete.model.ReasonChooseViewData
import com.likeminds.feedsample.delete.view.adapter.ReasonChooseAdapter.ReasonChooseAdapterListener
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_REASON_CHOOSE

class ReasonChooseViewDataBinder constructor(
    private val reasonChooseAdapterListener: ReasonChooseAdapterListener,
) : ViewDataBinder<ItemReasonChooseBinding, ReasonChooseViewData>() {

    override val viewType: Int
        get() = ITEM_REASON_CHOOSE

    override fun createBinder(parent: ViewGroup): ItemReasonChooseBinding {
        return ItemReasonChooseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemReasonChooseBinding,
        data: ReasonChooseViewData,
        position: Int
    ) {
        binding.viewData = data

        binding.root.setOnClickListener {
            val viewData = binding.viewData ?: return@setOnClickListener
            reasonChooseAdapterListener.onOptionSelected(viewData)
        }
    }
}