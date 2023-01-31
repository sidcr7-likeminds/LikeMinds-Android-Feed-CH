package com.likeminds.feedsx.post.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemCreatePostDocumentBinding
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM

class ItemCreatePostDocumentViewDataBinder :
    ViewDataBinder<ItemCreatePostDocumentBinding, AttachmentViewData>() {
    override val viewType: Int
        get() = ITEM_CREATE_POST_DOCUMENTS_ITEM

    override fun createBinder(parent: ViewGroup): ItemCreatePostDocumentBinding {
        return ItemCreatePostDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemCreatePostDocumentBinding,
        data: AttachmentViewData,
        position: Int
    ) {

        // sets the document data on the doc view
        initDocument(binding, data)
    }

    // initializes document item of the document recyclerview
    private fun initDocument(
        binding: ItemCreatePostDocumentBinding,
        document: AttachmentViewData,
    ) {
        binding.tvMeta1.hide()
        binding.viewMetaDot1.hide()
        binding.tvMeta2.hide()
        binding.viewMetaDot2.hide()
        binding.tvMeta3.hide()

        val attachmentMeta = document.attachmentMeta

        val noOfPage = attachmentMeta.pageCount ?: 0
        val mediaType = attachmentMeta.format
        if (noOfPage > 0) {
            binding.tvMeta1.show()
            binding.tvMeta1.text = binding.root.context.getString(
                R.string.placeholder_pages, noOfPage
            )
        }
        if (!attachmentMeta.size.isNullOrEmpty()) {
            binding.tvMeta2.show()
            binding.tvMeta2.text = attachmentMeta.size
            if (binding.tvMeta1.isVisible) {
                binding.viewMetaDot1.show()
            }
        }
        if (!mediaType.isNullOrEmpty() && (binding.tvMeta1.isVisible || binding.tvMeta2.isVisible)) {
            binding.tvMeta3.show()
            binding.tvMeta3.text = mediaType
            binding.viewMetaDot2.show()
        }
        binding.ivCross.setOnClickListener {
            //TODO:
        }
    }
}