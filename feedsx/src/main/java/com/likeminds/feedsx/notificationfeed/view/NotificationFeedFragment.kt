package com.likeminds.feedsx.notificationfeed.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.databinding.LmFeedFragmentNotificationFeedBinding
import com.likeminds.feedsx.notificationfeed.model.ActivityViewData
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter.NotificationFeedAdapterListener
import com.likeminds.feedsx.notificationfeed.viewmodel.NotificationFeedViewModel
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.model.BaseViewType
import kotlinx.coroutines.flow.onEach

class NotificationFeedFragment :
    BaseFragment<LmFeedFragmentNotificationFeedBinding, NotificationFeedViewModel>(),
    NotificationFeedAdapterListener {

    private lateinit var mNotificationFeedAdapter: NotificationFeedAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mScrollListener: EndlessRecyclerScrollListener

    override fun getViewModelClass(): Class<NotificationFeedViewModel> {
        return NotificationFeedViewModel::class.java
    }

    override fun getViewBinding(): LmFeedFragmentNotificationFeedBinding {
        return LmFeedFragmentNotificationFeedBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().notificationFeedComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()

        initData()
        initRecyclerView()
        initSwipeRefreshLayout()
    }

    override fun observeData() {
        super.observeData()

        // observes get notification feed response
        viewModel.notificationFeedResponse.observe(viewLifecycleOwner) { pair ->
            observeNotificationFeed(pair)
        }

        viewModel.errorMessageEventFlow.onEach { response ->
            observeErrorMessage(response)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    private fun initData() {
        ProgressHelper.showProgress(binding.progressBar)
        viewModel.getNotificationFeed(1)
    }

    // initializes notification recycler view
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mNotificationFeedAdapter = NotificationFeedAdapter(this)
        binding.rvNotifications.apply {
            layoutManager = linearLayoutManager
            adapter = mNotificationFeedAdapter
            show()
        }

        attachScrollListener(
            binding.rvNotifications,
            linearLayoutManager
        )
    }

    //attach scroll listener for pagination
    private fun attachScrollListener(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        mScrollListener = object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (currentPage > 0) {
                    // calls api for paginated data
                    viewModel.getNotificationFeed(currentPage)
                }
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)
    }

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            LMBranding.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            refreshNotificationFeed()
        }
    }

    //refresh the whole notification feed
    private fun refreshNotificationFeed() {
        mSwipeRefreshLayout.isRefreshing = true
        mScrollListener.resetData()
        viewModel.getNotificationFeed(1)
    }

    private fun observeNotificationFeed(pair: Pair<Int, List<ActivityViewData>>) {
        //hide progress bar
        ProgressHelper.hideProgress(binding.progressBar)

        //page in api send
        val page = pair.first

        //list of activities
        val activities = pair.second

        //if pull to refresh is called
        if (mSwipeRefreshLayout.isRefreshing) {
            setFeedAndScrollToTop(activities)
            mSwipeRefreshLayout.isRefreshing = false
            return
        }

        //normal adding
        if (page == 1) {
            checkForNoActivity(activities)
            setFeedAndScrollToTop(activities)
        } else {
            mNotificationFeedAdapter.addAll(activities)
        }
    }

    private fun observeErrorMessage(response: NotificationFeedViewModel.ErrorMessageEvent) {
        when (response) {
            is NotificationFeedViewModel.ErrorMessageEvent.GetNotificationFeed -> {
                val errorMessage = response.errorMessage
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                requireActivity().finish()
            }
            is NotificationFeedViewModel.ErrorMessageEvent.MarkReadNotification -> {
                val errorMessage = response.errorMessage
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }
        }
    }

    //set activities through diff utils and scroll to top of the feed
    private fun setFeedAndScrollToTop(feed: List<ActivityViewData>) {
        mNotificationFeedAdapter.replace(feed)
        binding.rvNotifications.scrollToPosition(0)
    }

    // checks if there is any activity or not
    private fun checkForNoActivity(feed: List<BaseViewType>) {
        if (feed.isNotEmpty()) {
            binding.apply {
                layoutNoNotification.root.hide()
                rvNotifications.show()
            }
        } else {
            binding.apply {
                layoutNoNotification.root.show()
                rvNotifications.hide()
            }
        }
    }

    override fun onNotificationFeedItemClicked(position: Int, activityViewData: ActivityViewData) {
        // mark the notification as read
        val updatedActivityViewData = activityViewData.toBuilder()
            .isRead(true)
            .build()
        mNotificationFeedAdapter.update(position, updatedActivityViewData)

        // call api to mark notification as read
        viewModel.markReadNotification(activityViewData.id)

        // handle route
        val routeIntent = Route.getRouteIntent(
            requireContext(),
            activityViewData.cta,
        )
        if (routeIntent != null) {
            requireContext().startActivity(routeIntent)
        }
    }
}