package com.likeminds.feedsx.topic.view

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedFragmentTopicSelectionBinding
import com.likeminds.feedsx.topic.adapter.LMFeedTopicSelectionAdapter
import com.likeminds.feedsx.topic.adapter.LMFeedTopicSelectionAdapterListener
import com.likeminds.feedsx.topic.model.LMFeedAllTopicsViewData
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionExtras
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionActivity.Companion.TOPIC_SELECTION_RESULT_EXTRAS
import com.likeminds.feedsx.topic.viewmodel.LMFeedTopicSelectionViewModel
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.emptyExtrasException

class LMFeedTopicSelectionFragment :
    BaseFragment<LmFeedFragmentTopicSelectionBinding, LMFeedTopicSelectionViewModel>(),
    LMFeedTopicSelectionAdapterListener {
    override fun getViewModelClass(): Class<LMFeedTopicSelectionViewModel> {
        return LMFeedTopicSelectionViewModel::class.java
    }

    override fun getViewBinding(): LmFeedFragmentTopicSelectionBinding {
        return LmFeedFragmentTopicSelectionBinding.inflate(layoutInflater)
    }

    private lateinit var extras: LMFeedTopicSelectionExtras
    private lateinit var mAdapter: LMFeedTopicSelectionAdapter

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
        initListeners()
        fetchTopics()
    }

    override fun observeData() {
        super.observeData()

        viewModel.topicsViewData.observe(viewLifecycleOwner) { response ->
            val page = response.first
            val topics = response.second

            if (page == 1) {
                mAdapter.replace(topics)
            } else {
                mAdapter.addAll(topics)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
        }
    }

    override fun allTopicSelected(lmFeedAllTopic: LMFeedAllTopicsViewData, position: Int) {
        if (lmFeedAllTopic.isSelected) return

        //update lmFeedAllTopic
        val updateAllTopic = lmFeedAllTopic.toBuilder()
            .isSelected(true)
            .build()

        //update recyclerview for all topics
        mAdapter.update(position, updateAllTopic)

        //update other topics
        mAdapter.items().forEachIndexed { index, topic ->
            if (topic is LMFeedTopicViewData) {
                val updatedTopic = topic.toBuilder().isSelected(false).build()

                mAdapter.update(index, updatedTopic)
            }

        }
    }

    override fun topicSelected(topic: LMFeedTopicViewData, position: Int) {

        val allTopicViewData = mAdapter.items()?.find {
            it is LMFeedAllTopicsViewData
        } as? LMFeedAllTopicsViewData

        //check all topic filter exists and it is selected
        if (allTopicViewData != null && allTopicViewData.isSelected) {
            //update view data to not selected
            val updatedAllTopicViewData = allTopicViewData.toBuilder()
                .isSelected(false)
                .build()

            mAdapter.update(0, updatedAllTopicViewData)
        }

        //update topic
        val updatedTopic = if (topic.isSelected) {
            topic.toBuilder()
                .isSelected(false)
                .build()
        } else {
            topic.toBuilder()
                .isSelected(true)
                .build()
        }

        //update recycle
        mAdapter.update(position, updatedTopic)
    }

    //calls api
    private fun fetchTopics() {
        viewModel.getTopics(extras.showAllTopicFilter, 1, null)
    }

    //init listeners for done
    private fun initListeners() {
        binding.fabSelected.setOnClickListener {
            //check for all topic is selected
            val allTopicViewData = mAdapter.items()?.find {
                it is LMFeedAllTopicsViewData
            } as? LMFeedAllTopicsViewData

            val resultExtras = if (allTopicViewData != null && allTopicViewData.isSelected) {
                LMFeedTopicSelectionResultExtras.Builder()
                    .isAllTopicSelected(true)
                    .build()
            } else {
                val selectedTopics = mAdapter.items().filter {
                    it is LMFeedTopicViewData && it.isSelected
                }.map {
                    it as LMFeedTopicViewData
                }
                LMFeedTopicSelectionResultExtras.Builder()
                    .selectedTopics(selectedTopics)
                    .build()
            }
            val resultIntent = Intent().apply {
                putExtra(TOPIC_SELECTION_RESULT_EXTRAS, resultExtras)
            }
            requireActivity().setResult(Activity.RESULT_OK, resultIntent)
            requireActivity().finish()
        }
    }

    //init recycler view
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        mAdapter = LMFeedTopicSelectionAdapter(this)
        binding.rvTopics.apply {
            layoutManager = linearLayoutManager
            adapter = mAdapter
            show()

            //attach scroll listener
            attachScrollListener(this, linearLayoutManager)
        }
    }

    // attach scroll listener for pagination
    private fun attachScrollListener(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (currentPage > 0) {
                    // calls api for paginated data
                    viewModel.getTopics(extras.showAllTopicFilter, currentPage, null)
                }
            }
        })
    }
}