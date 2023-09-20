package com.likeminds.feedsx.post.create.view

import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedDialogCreateResourceOptionsBinding
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.customview.BaseBottomSheetFragment

class LMFeedCreateResourceDialog :
    BaseBottomSheetFragment<LmFeedDialogCreateResourceOptionsBinding, Nothing>() {

    private lateinit var createResourceDialogListener: CreateResourceDialogListener

    companion object {
        private const val TAG = "LMFeedCreateResourceDialog"

        @JvmStatic
        fun show(fragmentManager: FragmentManager): LMFeedCreateResourceDialog {
            val sheet = LMFeedCreateResourceDialog()
            sheet.show(fragmentManager, TAG)
            return sheet
        }
    }

    override fun getViewModelClass(): Class<Nothing>? {
        return null
    }

    override fun getViewBinding(): LmFeedDialogCreateResourceOptionsBinding {
        return LmFeedDialogCreateResourceOptionsBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().feedComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeListeners()
    }

    // initializes listeners to the UI elements
    private fun initializeListeners() {
        binding.apply {
            try {
                createResourceDialogListener = parentFragment as CreateResourceDialogListener
            } catch (e: ClassCastException) {
                throw ClassCastException("Calling fragment must implement CreateResourceDialogListener interface")
            }

            llAddArticle.setOnClickListener {
                createResourceDialogListener.onResourceSelected(ARTICLE)
            }

            llAddVideo.setOnClickListener {
                createResourceDialogListener.onResourceSelected(VIDEO)
            }

            llAddPdf.setOnClickListener {
                createResourceDialogListener.onResourceSelected(DOCUMENT)
            }

            llAddLink.setOnClickListener {
                createResourceDialogListener.onResourceSelected(LINK)
            }
        }
    }

    interface CreateResourceDialogListener {
        fun onResourceSelected(@AttachmentType attachmentType: Int)
    }
}