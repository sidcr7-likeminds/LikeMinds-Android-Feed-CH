package com.likeminds.feedsx.delete.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.DialogFragmentDeleteBinding
import com.likeminds.feedsx.delete.model.DELETE_TYPE_POST
import com.likeminds.feedsx.delete.model.DeleteExtras
import com.likeminds.feedsx.delete.model.ReasonChooseViewData
import com.likeminds.feedsx.utils.customview.BaseDialogFragment

class DeleteDialogFragment : BaseDialogFragment<DialogFragmentDeleteBinding>(),
    ReasonChooseDialog.ReasonChooseDialogListener {

    companion object {
        private const val TAG = "DeleteContentDialogFragment"
        private const val ARG_DELETE_EXTRAS = "ARG_DELETE_EXTRAS"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            deleteExtras: DeleteExtras
        ) {
            DeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DELETE_EXTRAS, deleteExtras)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private var deleteDialogListener: DeleteDialogListener? = null

    private var deleteExtras: DeleteExtras? = null

    override fun getViewBinding(): DialogFragmentDeleteBinding {
        return DialogFragmentDeleteBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            deleteDialogListener = parentFragment as DeleteDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement DeleteDialogListener interface")
        }
    }

    override fun receiveExtras() {
        super.receiveExtras()
        arguments?.let {
            deleteExtras = it.getParcelable(ARG_DELETE_EXTRAS)
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
        initializeListeners()
    }

    // sets data as per content type [COMMENT/POST]
    private fun initView() {
        if (deleteExtras?.entityType == DELETE_TYPE_POST) {
            binding.tvTitle.text = getString(R.string.delete_post_question)
            binding.tvDescription.text = getString(R.string.delete_post_message)
        } else {
            binding.tvTitle.text = getString(R.string.delete_comment_question)
            binding.tvDescription.text = getString(R.string.delete_comment_message)
        }
    }

    // sets click listeners to select reason and submit request
    private fun initializeListeners() {
        binding.cvReason.setOnClickListener {
            ReasonChooseDialog.newInstance(childFragmentManager)
        }

        binding.tvConfirm.setOnClickListener {
            val data = binding.reasonData ?: return@setOnClickListener
            val reason = binding.etOtherReason.text.toString()
            if (data.value.isNotEmpty()) {
                if (data.value == "Others" && reason.isEmpty()) {
                    return@setOnClickListener
                }
            }

            deleteDialogListener?.delete(
                deleteExtras!!,
                data.tagId,
                reason
            )
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.etOtherReason.doAfterTextChanged {
            val reason = it.toString().trim()
            handleConfirmButton(reason.isNotEmpty())
        }
    }

    // handles confirm button (enabled/disabled)
    private fun handleConfirmButton(isEnabled: Boolean) {
        binding.tvConfirm.isEnabled = isEnabled
        if (isEnabled) {
            binding.tvConfirm.setTextColor(BrandingData.getButtonsColor())
        } else {
            binding.tvConfirm.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black_20
                )
            )
        }
    }

    // callback when a reason is selected from bottom sheet
    override fun onReasonSelected(viewData: ReasonChooseViewData) {
        binding.reasonData = viewData
        binding.tvReason.text = viewData.value
        binding.etOtherReason.setText("")

        if (viewData.value == "Others") {
            binding.cvOtherReason.visibility = View.VISIBLE
            handleConfirmButton(false)
        } else {
            binding.cvOtherReason.visibility = View.GONE
            handleConfirmButton(true)
        }
    }

    interface DeleteDialogListener {
        fun delete(
            deleteExtras: DeleteExtras,
            reportTagId: String,
            reason: String
        )
    }
}