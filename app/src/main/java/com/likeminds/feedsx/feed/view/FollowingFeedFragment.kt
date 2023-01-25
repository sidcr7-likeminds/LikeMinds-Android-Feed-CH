package com.likeminds.feedsx.feed.view

import com.likeminds.feedsx.databinding.FragmentFollowingFeedBinding
import com.likeminds.feedsx.utils.customview.BaseFragment

class FollowingFeedFragment : BaseFragment<FragmentFollowingFeedBinding>() {

    companion object {
        //TODO: Bundles
        fun getInstance(): FollowingFeedFragment {
            return FollowingFeedFragment()
        }
    }

    override fun getViewBinding(): FragmentFollowingFeedBinding {
        return FragmentFollowingFeedBinding.inflate(layoutInflater)
    }

}