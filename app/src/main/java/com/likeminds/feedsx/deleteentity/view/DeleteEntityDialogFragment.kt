package com.likeminds.feedsx.deleteentity.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.DialogFragmentDeleteEntityBinding
import com.likeminds.feedsx.deleteentity.model.DELETE_ENTITY_TYPE_POST
import com.likeminds.feedsx.deleteentity.model.DeleteEntityExtras
import com.likeminds.feedsx.deleteentity.model.ReasonChooseViewData
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseDialogFragment

class DeleteEntityDialogFragment : BaseDialogFragment<DialogFragmentDeleteEntityBinding>(),
    ReasonChooseDialog.ReasonChooseDialogListener {

    companion object {
        private const val TAG = "DeleteContentDialogFragment"
        private const val ARG_DELETE_CONTENT_EXTRAS = "ARG_DELETE_CONTENT_EXTRAS"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            deleteEntityExtras: DeleteEntityExtras
        ) {
            DeleteEntityDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DELETE_CONTENT_EXTRAS, deleteEntityExtras)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private var deleteChatRoomDialogListener: DeleteContentDialogListener? = null

    private var deleteEntityExtras: DeleteEntityExtras? = null

    override fun getViewBinding(): DialogFragmentDeleteEntityBinding {
        return DialogFragmentDeleteEntityBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        arguments?.let {
            deleteEntityExtras = it.getParcelable(ARG_DELETE_CONTENT_EXTRAS)
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
        initializeListeners()
    }

    // initializes window and sets data as per content type [COMMENT/POST]
    private fun initView() {
        val background = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(background, ViewUtils.dpToPx(32), 0, ViewUtils.dpToPx(32), 0)
        dialog?.window?.setBackgroundDrawable(inset)
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        if (deleteEntityExtras?.entityType == DELETE_ENTITY_TYPE_POST) {
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
            if (deleteEntityExtras == null) {
                return@setOnClickListener
            }
            val data = binding.reasonData ?: return@setOnClickListener
            val reason = binding.etOtherReason.text.toString()
            if (data.value.isNotEmpty()) {
                if (data.value == "Others" && reason.isEmpty()) {
                    return@setOnClickListener
                }
            }

            deleteChatRoomDialogListener?.deleteContent(
                deleteEntityExtras!!,
                data.tagId,
                reason
            )
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.etOtherReason.doAfterTextChanged {
            handleConfirmButton(it.toString().trim().isNotEmpty())
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

    interface DeleteContentDialogListener {
        fun deleteContent(
            deleteEntityExtras: DeleteEntityExtras,
            reportTagId: String,
            reason: String
        )
    }
}