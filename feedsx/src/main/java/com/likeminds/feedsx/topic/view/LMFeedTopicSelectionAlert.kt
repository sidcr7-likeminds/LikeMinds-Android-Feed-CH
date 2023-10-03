package com.likeminds.feedsx.topic.view

import androidx.fragment.app.FragmentManager
import com.likeminds.feedsx.databinding.LmFeedDialogFragmentTopicNotSelectedBinding
import com.likeminds.feedsx.utils.customview.BaseDialogFragment

class LMFeedTopicSelectionAlert :
    BaseDialogFragment<LmFeedDialogFragmentTopicNotSelectedBinding>() {

    companion object {

        private const val TAG = "LMFeedTopicSelectionAlert"

        @JvmStatic
        fun showDialog(supportFragmentManager: FragmentManager) {
            LMFeedTopicSelectionAlert().show(supportFragmentManager, TAG)
        }
    }

    override fun getViewBinding(): LmFeedDialogFragmentTopicNotSelectedBinding {
        return LmFeedDialogFragmentTopicNotSelectedBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initializeListeners()
    }

    private fun initializeListeners() {
        // submits post delete request and triggers callback
        binding.tvOk.setOnClickListener {
            dismiss()
        }
    }

}