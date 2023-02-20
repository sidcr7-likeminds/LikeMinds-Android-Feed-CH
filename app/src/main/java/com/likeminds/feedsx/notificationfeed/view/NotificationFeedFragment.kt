package com.likeminds.feedsx.notificationfeed.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.FragmentNotificationFeedBinding
import com.likeminds.feedsx.notificationfeed.model.NotificationFeedViewData
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter.NotificationFeedAdapterListener
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFeedFragment :
    BaseFragment<FragmentNotificationFeedBinding>(),
    NotificationFeedAdapterListener {

    private lateinit var mNotificationFeedAdapter: NotificationFeedAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    override fun getViewBinding(): FragmentNotificationFeedBinding {
        return FragmentNotificationFeedBinding.inflate(layoutInflater)
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

        attachPagination(
            binding.rvNotifications,
            linearLayoutManager
        )

        //TODO: testing data
        addTestingData()
    }

    //TODO: testing data
    private fun addTestingData() {
        val user = UserViewData.Builder()
            .name("Sid")
            .customTitle("Admin")
            .build()

        var text = "Nishkarsh Kaushik commented on your post with photo."
        mNotificationFeedAdapter.add(
            NotificationFeedViewData.Builder()
                .id("1")
                .user(user)
//                .menuItems(
//                    listOf(
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("1")
//                            .title("Delete")
//                            .build(),
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("2")
//                            .title("Mute")
//                            .build()
//                    )
//                )
                .cta("route://post_detail?post_id=1&comment_id=2")
                .createdAt(1675721450000)
                .activityMessage(text)
                .build()
        )

        text = "Nishkarsh Kaushik liked your post with document."

        mNotificationFeedAdapter.add(
            NotificationFeedViewData.Builder()
                .id("2")
                .user(user)
//                .menuItems(
//                    listOf(
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("1")
//                            .title("Delete")
//                            .build(),
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("2")
//                            .title("Mute")
//                            .build()
//                    )
//                )
                .createdAt(1675717850000)
                .cta("route://create_post")
                .activityMessage(text)
                .build()
        )

        mNotificationFeedAdapter.add(
            NotificationFeedViewData.Builder()
                .id("3")
                .user(user)
                .isRead(true)
//                .menuItems(
//                    listOf(
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("1")
//                            .title("Delete")
//                            .build(),
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("2")
//                            .title("Mute")
//                            .build()
//                    )
//                )
                .createdAt(1675458650000)
                .activityMessage(text)
                .build()
        )

        mNotificationFeedAdapter.add(
            NotificationFeedViewData.Builder()
                .id("4")
                .user(user)
                .isRead(true)
//                .menuItems(
//                    listOf(
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("1")
//                            .title("Delete")
//                            .build(),
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("2")
//                            .title("Mute")
//                            .build()
//                    )
//                )
                .createdAt(1670274650000)
                .activityMessage(text)
                .build()
        )

        mNotificationFeedAdapter.add(
            NotificationFeedViewData.Builder()
                .id("5")
                .user(user)
                .isRead(true)
//                .menuItems(
//                    listOf(
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("1")
//                            .title("Delete")
//                            .build(),
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("2")
//                            .title("Mute")
//                            .build()
//                    )
//                )
                .createdAt(1638738650000)
                .activityMessage(text)
                .build()
        )
    }

    //attach scroll listener for pagination
    private fun attachPagination(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                // TODO: add logic
            }
        })
    }

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            BrandingData.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = true
            fetchRefreshedData()
        }
    }

    //TODO: Call api and refresh the notification data
    private fun fetchRefreshedData() {
        val user = UserViewData.Builder()
            .name("Sid")
            .customTitle("Admin")
            .build()

        var text = "Nishkarsh Kaushik commented on your post with photo."
        mNotificationFeedAdapter.add(
            0,
            NotificationFeedViewData.Builder()
                .id("1")
                .user(user)
//                .menuItems(
//                    listOf(
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("1")
//                            .title("Delete")
//                            .build(),
//                        OverflowMenuItemViewData.Builder()
//                            .entityId("2")
//                            .title("Mute")
//                            .build()
//                    )
//                )
                .cta("route://post_detail?post_id=1&comment_id=2")
                .createdAt(1675721450000)
                .activityMessage(text)
                .build()
        )
        mSwipeRefreshLayout.isRefreshing = false
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        //TODO: handle menu clicks
    }
}