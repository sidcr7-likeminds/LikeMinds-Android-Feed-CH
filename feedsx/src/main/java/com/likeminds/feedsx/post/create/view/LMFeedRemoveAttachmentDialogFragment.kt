package com.likeminds.feedsx.post.create.view

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedDialogFragmentRemoveAttachmentBinding
import com.likeminds.feedsx.post.create.model.LMFeedRemoveDialogExtras
import com.likeminds.feedsx.utils.customview.BaseDialogFragment

class LMFeedRemoveAttachmentDialogFragment :
    BaseDialogFragment<LmFeedDialogFragmentRemoveAttachmentBinding>() {

    companion object {
        private const val TAG = "LMFeedRemoveAttachmentDialogFragment"
        private lateinit var removeDialogExtras: LMFeedRemoveDialogExtras

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            removeDialogExtras: LMFeedRemoveDialogExtras
        ): LMFeedRemoveAttachmentDialogFragment {
            val sheet = LMFeedRemoveAttachmentDialogFragment()
            sheet.show(supportFragmentManager, TAG)
            this.removeDialogExtras = removeDialogExtras
            return sheet
        }
    }

    private lateinit var removeAttachmentDialogListener: RemoveAttachmentDialogListener

    override fun getViewBinding(): LmFeedDialogFragmentRemoveAttachmentBinding {
        return LmFeedDialogFragmentRemoveAttachmentBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().createPostComponent()?.inject(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            removeAttachmentDialogListener = parentFragment as RemoveAttachmentDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement RemoveAttachmentDialogListener interface")
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
    }

    // sets data as per extras
    private fun initView() {
        binding.apply {
            tvTitle.text = removeDialogExtras.title
            tvDescription.text = removeDialogExtras.description
            tvRemove.setOnClickListener {
                removeAttachmentDialogListener.onRemoved()
            }
            tvCancel.setOnClickListener {
                removeAttachmentDialogListener.onCancelled()
            }
        }
    }


    interface RemoveAttachmentDialogListener {
        fun onRemoved()
        fun onCancelled()
    }
}