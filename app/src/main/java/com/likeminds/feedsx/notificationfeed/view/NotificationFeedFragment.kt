package com.likeminds.feedsx.notificationfeed.view

import com.likeminds.feedsx.databinding.FragmentNotificationFeedBinding
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFeedFragment : BaseFragment<FragmentNotificationFeedBinding>() {

    override fun getViewBinding(): FragmentNotificationFeedBinding {
        return FragmentNotificationFeedBinding.inflate(layoutInflater)
    }
}