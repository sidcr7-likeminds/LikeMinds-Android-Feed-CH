package com.likeminds.feedsx.post.edit.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemDocumentBinding
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.utils.AndroidUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_EDIT_POST_DOCUMENTS_ITEM

class EditPostDocumentViewDataBinder : ViewDataBinder<ItemDocumentBinding, AttachmentViewData>() {
    override val viewType: Int
        get() = ITEM_EDIT_POST_DOCUMENTS_ITEM

    override fun createBinder(parent: ViewGroup): ItemDocumentBinding {
        return ItemDocumentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemDocumentBinding,
        data: AttachmentViewData,
        position: Int
    ) {
        // sets the document data on the doc view
        initDocument(binding, data)
    }

    // initializes document item of the document recyclerview
    private fun initDocument(
        binding: ItemDocumentBinding,
        document: AttachmentViewData
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
                tvMeta1.text = root.context.resources.getQuantityString(
                    R.plurals.placeholder_pages,
                    noOfPage,
                    noOfPage
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

            root.setOnClickListener {
                val pdfUri = document.attachmentMeta.uri
                if (pdfUri != null) {
                    AndroidUtils.startDocumentViewer(root.context, pdfUri)
                }
            }
        }
    }
}