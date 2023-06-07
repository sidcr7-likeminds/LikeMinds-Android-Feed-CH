package com.likeminds.feedsx.notificationfeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemNotificationFeedBinding
import com.likeminds.feedsx.notificationfeed.model.ActivityViewData
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter.NotificationFeedAdapterListener
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.Route
import com.likeminds.feedsx.utils.TimeUtil.getRelativeTime
import com.likeminds.feedsx.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_NOTIFICATION_FEED

class ItemNotificationFeedViewDataBinder constructor(
    val listener: NotificationFeedAdapterListener
) : ViewDataBinder<ItemNotificationFeedBinding, ActivityViewData>() {

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
        data: ActivityViewData,
        position: Int
    ) {

        // set items to overflow menu

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
        data: ActivityViewData
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
                // show overflow menu
            }

            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                data.id,
                memberImage,
                showRoundImage = true
            )

            // find the type of post and set drawable accordingly
            ivPostType.setImageResource(R.drawable.ic_doc_attachment)

            tvNotificationDate.text = getRelativeTime(data.createdAt)
        }
    }

    // handles text content of notification
    private fun initNotificationTextContent(
        binding: ItemNotificationFeedBinding,
        data: ActivityViewData
    ) {
        binding.apply {
            val textForLinkify = data.activityText.getValidTextForLinkify()

            tvNotificationContent.text = textForLinkify
        }
    }

    private fun handleRoute(
        binding: ItemNotificationFeedBinding,
        data: ActivityViewData
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