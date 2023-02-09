package com.likeminds.feedsx.deleteentity.view

import android.graphics.Color
import android.view.View
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.databinding.DialogReasonChooseBinding
import com.likeminds.feedsx.deleteentity.model.ReasonChooseViewData
import com.likeminds.feedsx.deleteentity.view.adapter.ReasonChooseAdapter
import com.likeminds.feedsx.deleteentity.view.adapter.ReasonChooseAdapter.ReasonChooseAdapterListener
import com.likeminds.feedsx.utils.customview.BaseBottomSheetFragment
import com.likeminds.feedsx.utils.model.BaseViewType

class ReasonChooseDialog : BaseBottomSheetFragment<DialogReasonChooseBinding>(),
    ReasonChooseAdapterListener {

    companion object {
        private const val TAG = "ReasonChooseDialog"

        @JvmStatic
        fun newInstance(fragmentManager: FragmentManager) =
            ReasonChooseDialog().show(fragmentManager, TAG)
    }

    lateinit var reasonChooseAdapter: ReasonChooseAdapter

    private var reasonChooseDialogListener: ReasonChooseDialogListener? = null

    override fun getViewBinding(): DialogReasonChooseBinding {
        return DialogReasonChooseBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeUI()
    }

    // initialized listener and adds data to the list
    private fun initializeUI() {
        try {
            reasonChooseDialogListener = parentFragment as ReasonChooseDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement ReasonChooseDialogListener interface")
        }

        (binding.root.parent as View).setBackgroundColor(Color.TRANSPARENT)

        //TODO: testing data
        val list = ArrayList<ReasonChooseViewData>(
            listOf(
                ReasonChooseViewData.Builder().value("Spam").build(),
                ReasonChooseViewData.Builder().value("Hate Speech").build(),
                ReasonChooseViewData.Builder().value("Nudity").build(),
                ReasonChooseViewData.Builder().value("Terrorism").build(),
                ReasonChooseViewData.Builder().value("Others").build()
            )
        )

        reasonChooseAdapter = ReasonChooseAdapter(this)
        binding.rvReasons.apply {
            setHasFixedSize(true)
            adapter = reasonChooseAdapter
        }
        reasonChooseAdapter.replace(list as List<BaseViewType>?)
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