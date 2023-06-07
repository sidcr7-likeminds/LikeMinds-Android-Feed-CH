package com.likeminds.feedsx.notificationfeed.view.adapter.databinder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ItemNotificationFeedBinding
import com.likeminds.feedsx.notificationfeed.model.ActivityViewData
import com.likeminds.feedsx.notificationfeed.view.adapter.NotificationFeedAdapter.NotificationFeedAdapterListener
import com.likeminds.feedsx.posttypes.model.DOCUMENT
import com.likeminds.feedsx.posttypes.model.IMAGE
import com.likeminds.feedsx.posttypes.model.VIDEO
import com.likeminds.feedsx.utils.TimeUtil.getRelativeTime
import com.likeminds.feedsx.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.ITEM_NOTIFICATION_FEED

class ItemNotificationFeedViewDataBinder constructor(
    val listener: NotificationFeedAdapterListener
) : ViewDataBinder<ItemNotificationFeedBinding, ActivityViewData>() {

    override val viewType: Int
        get() = ITEM_NOTIFICATION_FEED

    override fun createBinder(parent: ViewGroup): ItemNotificationFeedBinding {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNotificationFeedBinding.inflate(
            inflater,
            parent,
            false
        )
        binding.root.setOnClickListener {
            val activityViewData = binding.activityViewData ?: return@setOnClickListener
            listener.onNotificationFeedItemClicked(binding.position, activityViewData)
        }
        return binding
    }

    override fun bindData(
        binding: ItemNotificationFeedBinding,
        data: ActivityViewData,
        position: Int
    ) {
        // set value for binding variables
        binding.position = position
        binding.activityViewData = data

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

        val user = data.activityEntityData.user
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

//            MemberImageUtil.setImage(
//                user.imageUrl,
//                user.name,
//                data.id,
//                memberImage,
//                showRoundImage = true
//            )

            // find the type of post and set drawable accordingly
            updatePostTypeBadge(this, data)

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

    private fun updatePostTypeBadge(
        binding: ItemNotificationFeedBinding,
        data: ActivityViewData
    ) {
        binding.apply {
            val attachments = data.activityEntityData.attachments
            if (!attachments.isNullOrEmpty()) {
                when (attachments.first().attachmentType) {
                    IMAGE -> {
                        ivPostType.show()
                        ivPostType.setImageResource(R.drawable.ic_add_image)
                    }
                    VIDEO -> {
                        ivPostType.show()
                        ivPostType.setImageResource(R.drawable.ic_add_image)
                    }
                    DOCUMENT -> {
                        ivPostType.show()
                        ivPostType.setImageResource(R.drawable.ic_doc_attachment)
                    }
                    else -> {
                        ivPostType.hide()
                    }
                }
            }
        }
    }
}