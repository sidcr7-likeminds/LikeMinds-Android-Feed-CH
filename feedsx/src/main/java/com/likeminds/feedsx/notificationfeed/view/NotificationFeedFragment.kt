package com.likeminds.feedsx.notificationfeed.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.databinding.FragmentNotificationFeedBinding
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter.NotificationFeedAdapterListener
import com.likeminds.feedsx.notificationfeed.viewmodel.NotificationFeedViewModel
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment

class NotificationFeedFragment :
    BaseFragment<FragmentNotificationFeedBinding, NotificationFeedViewModel>(),
    NotificationFeedAdapterListener {

    private lateinit var mNotificationFeedAdapter: NotificationFeedAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun getViewModelClass(): Class<NotificationFeedViewModel> {
        return NotificationFeedViewModel::class.java
    }

    override fun getViewBinding(): FragmentNotificationFeedBinding {
        return FragmentNotificationFeedBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().notificationFeedComponent()?.inject(this)
    }

    override fun setUpViews() {
        super.setUpViews()

        initRecyclerView()
        initSwipeRefreshLayout()
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
        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                // logic  for pagination goes here
            }
        })
    }

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            LMBranding.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = true
            fetchRefreshedData()
        }
    }

    private fun fetchRefreshedData() {
        // fetch refreshed data here
        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        // handle the click on post menu
    }
}