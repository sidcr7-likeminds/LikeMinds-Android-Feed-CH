package com.likeminds.feedsx.feed.view

import com.likeminds.feedsx.databinding.FragmentUniversalFeedBinding
import com.likeminds.feedsx.utils.customview.BaseFragment

class UniversalFeedFragment : BaseFragment<FragmentUniversalFeedBinding>() {

    companion object {
        //TODO: Bundles
        fun getInstance(): UniversalFeedFragment {
            return UniversalFeedFragment()
        }
    }

    override fun getViewBinding(): FragmentUniversalFeedBinding {
        return FragmentUniversalFeedBinding.inflate(layoutInflater)
    }
}