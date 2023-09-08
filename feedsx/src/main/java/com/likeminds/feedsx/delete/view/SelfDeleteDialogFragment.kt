package com.likeminds.feedsx.delete.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedDialogFragmentSelfDeleteBinding
import com.likeminds.feedsx.delete.model.DELETE_TYPE_POST
import com.likeminds.feedsx.delete.model.DeleteExtras
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.customview.BaseDialogFragment
import com.likeminds.feedsx.utils.emptyExtrasException

//when user deletes their own post
class SelfDeleteDialogFragment : BaseDialogFragment<LmFeedDialogFragmentSelfDeleteBinding>() {

    companion object {
        private const val TAG = "DeleteAlertDialogFragment"
        private const val ARG_DELETE_EXTRAS = "ARG_DELETE_EXTRAS"

        @JvmStatic
        fun showDialog(
            supportFragmentManager: FragmentManager,
            deleteExtras: DeleteExtras
        ) {
            SelfDeleteDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_DELETE_EXTRAS, deleteExtras)
                }
            }.show(supportFragmentManager, TAG)
        }
    }

    private var deleteAlertDialogListener: DeleteAlertDialogListener? = null

    private lateinit var deleteExtras: DeleteExtras

    override fun getViewBinding(): LmFeedDialogFragmentSelfDeleteBinding {
        return LmFeedDialogFragmentSelfDeleteBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            deleteAlertDialogListener = parentFragment as DeleteAlertDialogListener?
        } catch (e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement DeleteAlertDialogListener interface")
        }
    }

    override fun receiveExtras() {
        super.receiveExtras()
        arguments?.let {
            deleteExtras = ExtrasUtil.getParcelable(
                it,
                ARG_DELETE_EXTRAS,
                DeleteExtras::class.java
            ) ?: throw emptyExtrasException(TAG)
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
        initializeListeners()
    }

    // sets data as per content type [COMMENT/POST]
    private fun initView() {
        if (deleteExtras.entityType == DELETE_TYPE_POST) {
            binding.tvTitle.text = getString(R.string.delete_post_question)
            binding.tvDescription.text = getString(R.string.delete_post_message)
        } else {
            binding.tvTitle.text = getString(R.string.delete_comment_question)
            binding.tvDescription.text = getString(R.string.delete_comment_message)
        }
    }

    // sets click listeners
    private fun initializeListeners() {

        // submits post delete request and triggers callback
        binding.tvDelete.setOnClickListener {
            deleteAlertDialogListener?.selfDelete(
                deleteExtras
            )
            dismiss()
        }

        // dismisses the delete dialog
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
    }

    interface DeleteAlertDialogListener {
        fun selfDelete(
            deleteExtras: DeleteExtras
        )
    }
}