package com.likeminds.feedsx.delete.view

import android.graphics.Color
import android.view.View
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedDialogReasonChooseBinding
import com.likeminds.feedsx.delete.model.ReasonChooseViewData
import com.likeminds.feedsx.delete.view.adapter.ReasonChooseAdapter
import com.likeminds.feedsx.delete.view.adapter.ReasonChooseAdapter.ReasonChooseAdapterListener
import com.likeminds.feedsx.delete.viewmodel.ReasonChooseViewModel
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseBottomSheetFragment

// bottom sheet dialog to show the reasons list
class LMFeedReasonChooseDialog :
    BaseBottomSheetFragment<LmFeedDialogReasonChooseBinding, ReasonChooseViewModel>(),
    ReasonChooseAdapterListener {

    companion object {
        private const val TAG = "ReasonChooseDialog"

        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager) =
            LMFeedReasonChooseDialog().show(fragmentManager, TAG)
    }

    private lateinit var reasonChooseAdapter: ReasonChooseAdapter

    private var reasonChooseDialogListener: ReasonChooseDialogListener? = null

    override fun getViewModelClass(): Class<ReasonChooseViewModel> {
        return ReasonChooseViewModel::class.java
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().reasonChooseComponent()?.inject(this)
    }

    override fun getViewBinding(): LmFeedDialogReasonChooseBinding {
        return LmFeedDialogReasonChooseBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeUI()
        initData()
    }

    // observes data
    override fun observeData() {
        super.observeData()

        // observes [listOfTagViewData] and replaces items in sheet
        viewModel.listOfTagViewData.observe(viewLifecycleOwner) { tags ->
            reasonChooseAdapter.replace(tags)
        }

        // observes [errorMessage] and shows error toast
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            dismiss()
        }
    }

    // initialized listener and adds data to the list
    private fun initializeUI() {
        try {
            reasonChooseDialogListener = parentFragment as ReasonChooseDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement ReasonChooseDialogListener interface")
        }

        (binding.root.parent as View).setBackgroundColor(Color.TRANSPARENT)

        reasonChooseAdapter = ReasonChooseAdapter(this)
        binding.rvReasons.apply {
            setHasFixedSize(true)
            adapter = reasonChooseAdapter
        }
    }

    // fetches report tags
    private fun initData() {
        viewModel.getReportTags()
    }

    // callback when one of the item from reason list is selected
    override fun onOptionSelected(viewData: ReasonChooseViewData) {
        reasonChooseDialogListener?.onReasonSelected(viewData)
        dismiss()
    }

    interface ReasonChooseDialogListener {
        fun onReasonSelected(viewData: ReasonChooseViewData)
    }
}