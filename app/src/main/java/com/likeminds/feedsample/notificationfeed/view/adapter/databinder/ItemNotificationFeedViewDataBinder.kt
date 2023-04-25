package com.likeminds.feedsample.notificationfeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feedsample.R
import com.likeminds.feedsample.databinding.ItemNotificationFeedBinding
import com.likeminds.feedsample.notificationfeed.model.NotificationFeedViewData
import com.likeminds.feedsample.notificationfeed.view.adapter.NotificationFeedAdapter.NotificationFeedAdapterListener
import com.likeminds.feedsample.utils.MemberImageUtil
import com.likeminds.feedsample.utils.Route
import com.likeminds.feedsample.utils.TimeUtil.getRelativeTime
import com.likeminds.feedsample.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.ITEM_NOTIFICATION_FEED

class ItemNotificationFeedViewDataBinder constructor(
    val listener: NotificationFeedAdapterListener
) : ViewDataBinder<ItemNotificationFeedBinding, NotificationFeedViewData>() {

    override val viewType: Int
        get() = ITEM_NOTIFICATION_FEED

    override fun createBinder(parent: ViewGroup): ItemNotificationFeedBinding {
        return ItemNotificationFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: ItemNotificationFeedBinding,
        data: NotificationFeedViewData,
        position: Int
    ) {

        // sets items to overflow menu
        //todo
//        PostTypeUtil.setOverflowMenuItems(
//            overflowMenu,
//            data.menuItems
//        )

        // handles route on notification click
        handleRoute(
            binding,
            data
        )

        // sets data to the notification item
        initNotificationView(
            binding,
            data
        )
    }

    // initializes notification item
    private fun initNotificationView(
        binding: ItemNotificationFeedBinding,
        data: NotificationFeedViewData
    ) {
        initNotificationTextContent(
            binding,
            data
        )

        val user = data.user
        binding.apply {
            if (data.isRead) {
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.white
                    )
                )
            } else {
                root.setBackgroundColor(
                    ContextCompat.getColor(
                        root.context,
                        R.color.cloudy_blue_40
                    )
                )
            }

            ivNotificationMenu.setOnClickListener {
                //todo
//                PostTypeUtil.showOverflowMenu(
//                    ivNotificationMenu,
//                    overflowMenu
//                )
            }

            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                data.id,
                memberImage,
                showRoundImage = true
            )

            //TODO: logic to get post type.
            ivPostType.setImageResource(R.drawable.ic_doc_attachment)

            tvNotificationDate.text = getRelativeTime(data.createdAt)
        }
    }

    // handles text content of notification
    private fun initNotificationTextContent(
        binding: ItemNotificationFeedBinding,
        data: NotificationFeedViewData
    ) {
        binding.apply {
            val textForLinkify = data.activityMessage.getValidTextForLinkify()

            // TODO: handle the member name in the text.
            tvNotificationContent.text = textForLinkify
        }
    }

    private fun handleRoute(
        binding: ItemNotificationFeedBinding,
        data: NotificationFeedViewData
    ) {
        binding.root.apply {
            setOnClickListener {
                val routeIntent = Route.getRouteIntent(
                    context,
                    data.cta,
                )
                if (routeIntent != null)
                    context.startActivity(routeIntent)
            }
        }
    }
}