package com.likeminds.feedsx.post.create.view

import android.content.Context
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedDialogFragmentLinkResourceBinding
import com.likeminds.feedsx.post.edit.viewmodel.HelperViewModel
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.utils.ValueUtils.getUrlIfExist
import com.likeminds.feedsx.utils.customview.BaseDialogFragment
import com.likeminds.feedsx.utils.observeInLifecycle
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class LMFeedLinkResourceDialogFragment :
    BaseDialogFragment<LmFeedDialogFragmentLinkResourceBinding>() {

    @Inject
    lateinit var helperViewModel: HelperViewModel

    private lateinit var linkResourceDialogListener: LinkResourceDialogListener

    companion object {
        private const val TAG = "LinkResourceDialogFragment"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager
        ) {
            LMFeedLinkResourceDialogFragment().show(supportFragmentManager, TAG)
        }
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().feedComponent()?.inject(this)
    }

    override fun getViewBinding(): LmFeedDialogFragmentLinkResourceBinding {
        return LmFeedDialogFragmentLinkResourceBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            linkResourceDialogListener = parentFragment as LinkResourceDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement LinkResourceDialogListener interface")
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeListeners()
    }

    override fun observeData() {
        super.observeData()

        helperViewModel.decodeUrlResponse.observe(viewLifecycleOwner) {
            linkResourceDialogListener.linkOgTags(it)
            this.dismiss()
        }

        helperViewModel.errorEventFlow.onEach { _ ->
            Toast.makeText(
                requireContext(),
                getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
            showLoader(false)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // initializes listeners
    private fun initializeListeners() {
        binding.apply {
            buttonColor = LMFeedBranding.getButtonsColor()
            tvConfirm.setOnClickListener {
                processInputLink()
            }

            etLink.doAfterTextChanged {
                tvConfirm.isEnabled = !(it.isNullOrEmpty())
            }

            tvCancel.setOnClickListener {
                this@LMFeedLinkResourceDialogFragment.dismiss()
            }
        }
    }

    // processes the input link by the user
    private fun processInputLink() {
        val text = binding.etLink.text.toString()
        val link = text.getUrlIfExist()
        if (!link.isNullOrEmpty()) {
            showLoader(true)
            helperViewModel.decodeUrl(link)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.please_enter_a_valid_link),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // shows and hides the loader
    private fun showLoader(show: Boolean) {
        binding.tvConfirm.isVisible = !show
        binding.pbLink.root.isVisible = show
    }

    interface LinkResourceDialogListener {
        fun linkOgTags(linkOGTags: LinkOGTagsViewData)
    }
}