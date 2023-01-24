package com.likeminds.feedsx.home.views

import com.likeminds.feedsx.databinding.FragmentFollowingBinding
import com.likeminds.feedsx.utils.customview.BaseFragment

class FollowingFragment : BaseFragment<FragmentFollowingBinding>() {

    companion object {
        //TODO: Bundles
        fun getInstance(): FollowingFragment {
            return FollowingFragment()
        }
    }

    override fun getViewBinding(): FragmentFollowingBinding {
        return FragmentFollowingBinding.inflate(layoutInflater)
    }

}