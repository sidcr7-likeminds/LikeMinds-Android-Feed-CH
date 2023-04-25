package com.likeminds.feedsample.post.create.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.feedsample.R
import com.likeminds.feedsample.databinding.ItemCreatePostDocumentBinding
import com.likeminds.feedsample.media.model.PDF
import com.likeminds.feedsample.media.util.MediaUtils
import com.likeminds.feedsample.post.create.util.CreatePostListener
import com.likeminds.feedsample.posttypes.model.AttachmentViewData
import com.likeminds.feedsample.utils.AndroidUtils
import com.likeminds.feedsample.utils.ViewUtils.hide
import com.likeminds.feedsample.utils.ViewUtils.show
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_CREATE_POST_DOCUMENTS_ITEM

class ItemCreatePostDocumentViewDataBinder constructor(
    private val listener: CreatePostListener
) : ViewDataBinder<ItemCreatePostDocumentBinding, AttachmentViewData>() {
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
        initDocument(binding, data, position)
    }

    // initializes document item of the document recyclerview
    private fun initDocument(
        binding: ItemCreatePostDocumentBinding,
        document: AttachmentViewData,
        position: Int
    ) {
        binding.apply {
            tvMeta1.hide()
            viewMetaDot1.hide()
            tvMeta2.hide()
            viewMetaDot2.hide()
            tvMeta3.hide()

            val attachmentMeta = document.attachmentMeta

            tvDocumentName.text = attachmentMeta.name ?: "Document"

            val noOfPage = attachmentMeta.pageCount ?: 0
            val mediaType = attachmentMeta.format
            if (noOfPage > 0) {
                tvMeta1.show()
                tvMeta1.text = binding.root.context.getString(
                    R.string.placeholder_pages, noOfPage
                )
            }
            if (attachmentMeta.size != null) {
                val size = MediaUtils.getFileSizeText(attachmentMeta.size)
                tvMeta2.show()
                tvMeta2.text = size
                if (tvMeta1.isVisible) {
                    viewMetaDot1.show()
                }
            }
            if (!mediaType.isNullOrEmpty() && (tvMeta1.isVisible || tvMeta2.isVisible)) {
                tvMeta3.show()
                tvMeta3.text = mediaType
                viewMetaDot2.show()
            }

            ivCross.setOnClickListener {
                listener.onMediaRemoved(position, PDF)
            }

            root.setOnClickListener {
                val pdfUri = document.attachmentMeta.uri
                if (pdfUri != null) {
                    AndroidUtils.startDocumentViewer(root.context, pdfUri)
                }
            }
        }
    }
}