package com.likeminds.feedsx.post.create.view

import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedDialogDiscardResourceBinding
import com.likeminds.feedsx.post.edit.viewmodel.LMFeedHelperViewModel
import com.likeminds.feedsx.utils.ValueUtils.pluralizeOrCapitalize
import com.likeminds.feedsx.utils.customview.BaseBottomSheetFragment
import com.likeminds.feedsx.utils.pluralize.model.WordAction
import javax.inject.Inject

class LMFeedDiscardResourceDialog :
    BaseBottomSheetFragment<LmFeedDialogDiscardResourceBinding, Nothing>() {

    private lateinit var discardResourceDialogListener: DiscardResourceDialogListener

    @Inject
    lateinit var lmFeedHelperViewModel: LMFeedHelperViewModel

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

    override fun setPostVariable() {
        super.setPostVariable()

        val postAsVariable = lmFeedHelperViewModel.getPostVariable()

        binding.apply {
            tvTitle.text = getString(
                R.string.discard_s,
                postAsVariable.pluralizeOrCapitalize(WordAction.FIRST_LETTER_CAPITAL_SINGULAR)
            )

            tvDescription.text = getString(
                R.string.discard_s_description,
                postAsVariable.pluralizeOrCapitalize(WordAction.ALL_SMALL_SINGULAR),
                postAsVariable.pluralizeOrCapitalize(WordAction.ALL_SMALL_PLURAL)
            )

            tvDiscardAction.text = getString(
                R.string.discard_this_s,
                postAsVariable.pluralizeOrCapitalize(WordAction.ALL_SMALL_SINGULAR)
            )

            tvContinueCreatingAction.text = getString(
                R.string.continue_creating_s,
                postAsVariable.pluralizeOrCapitalize(WordAction.ALL_SMALL_SINGULAR)
            )
        }
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