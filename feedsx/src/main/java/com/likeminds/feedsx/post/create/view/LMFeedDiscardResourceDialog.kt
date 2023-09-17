package com.likeminds.feedsx.post.create.view

import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedDialogDiscardResourceBinding
import com.likeminds.feedsx.utils.customview.BaseBottomSheetFragment

class LMFeedDiscardResourceDialog :
    BaseBottomSheetFragment<LmFeedDialogDiscardResourceBinding, Nothing>() {

    private lateinit var discardResourceDialogListener: DiscardResourceDialogListener

    companion object {
        private const val TAG = "LMFeedDiscardResourceDialog"

        @JvmStatic
        fun show(fragmentManager: FragmentManager): LMFeedDiscardResourceDialog {
            val sheet = LMFeedDiscardResourceDialog()
            sheet.show(fragmentManager, TAG)
            return sheet
        }
    }

    override fun getViewModelClass(): Class<Nothing>? {
        return null
    }

    override fun getViewBinding(): LmFeedDialogDiscardResourceBinding {
        return LmFeedDialogDiscardResourceBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().createPostComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeListeners()
    }

    // initializes click listeners
    private fun initializeListeners() {
        try {
            discardResourceDialogListener =
                parentFragment as DiscardResourceDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement DiscardResourceDialogListener interface")
        }

        binding.llDiscard.setOnClickListener {
            discardResourceDialogListener.onResourceDiscarded()
        }

        binding.llContinue.setOnClickListener {
            discardResourceDialogListener.onResourceCreationContinued()
        }
    }

    interface DiscardResourceDialogListener {
        fun onResourceDiscarded()
        fun onResourceCreationContinued()
    }
}