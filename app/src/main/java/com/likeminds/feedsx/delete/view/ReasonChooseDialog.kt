package com.likeminds.feedsx.delete.view

import android.graphics.Color
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.likeminds.feedsx.databinding.DialogReasonChooseBinding
import com.likeminds.feedsx.delete.model.ReasonChooseViewData
import com.likeminds.feedsx.delete.view.adapter.ReasonChooseAdapter
import com.likeminds.feedsx.delete.view.adapter.ReasonChooseAdapter.ReasonChooseAdapterListener
import com.likeminds.feedsx.delete.viewmodel.ReasonChooseViewModel
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseBottomSheetFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReasonChooseDialog : BaseBottomSheetFragment<DialogReasonChooseBinding>(),
    ReasonChooseAdapterListener {

    companion object {
        private const val TAG = "ReasonChooseDialog"

        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager) =
            ReasonChooseDialog().show(fragmentManager, TAG)
    }

    private val viewModel: ReasonChooseViewModel by viewModels()

    private lateinit var reasonChooseAdapter: ReasonChooseAdapter

    private var reasonChooseDialogListener: ReasonChooseDialogListener? = null

    override fun getViewBinding(): DialogReasonChooseBinding {
        return DialogReasonChooseBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeUI()
        initData()
    }

    override fun observeData() {
        super.observeData()

        viewModel.listOfTagViewData.observe(viewLifecycleOwner) { tags ->
            reasonChooseAdapter.replace(tags)
        }

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