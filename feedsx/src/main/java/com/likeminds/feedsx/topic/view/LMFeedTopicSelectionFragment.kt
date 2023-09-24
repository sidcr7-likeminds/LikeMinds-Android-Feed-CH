package com.likeminds.feedsx.topic.view

import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedFragmentTopicSelectionBinding
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionExtras
import com.likeminds.feedsx.topic.viewmodel.LMFeedTopicSelectionViewModel
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.emptyExtrasException

class LMFeedTopicSelectionFragment :
    BaseFragment<LmFeedFragmentTopicSelectionBinding, LMFeedTopicSelectionViewModel>() {
    override fun getViewModelClass(): Class<LMFeedTopicSelectionViewModel> {
        return LMFeedTopicSelectionViewModel::class.java
    }

    override fun getViewBinding(): LmFeedFragmentTopicSelectionBinding {
        return LmFeedFragmentTopicSelectionBinding.inflate(layoutInflater)
    }

    private lateinit var extras: LMFeedTopicSelectionExtras

    companion object {
        const val TAG = "LMFeedTopicSelectionFragment"
    }

    override fun attachDagger() {
        SDKApplication.getInstance().lmFeedTopicComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()

        extras = ExtrasUtil.getParcelable(
            arguments,
            LMFeedTopicSelectionActivity.TOPIC_SELECTION_EXTRAS,
            LMFeedTopicSelectionExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun setUpViews() {
        super.setUpViews()

        initRecyclerView()
    }

    override fun observeData() {
        super.observeData()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())

    }
}