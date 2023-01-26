package com.likeminds.feedsx.feed.view

import com.likeminds.feedsx.databinding.FragmentLikesBinding
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesFragment : BaseFragment<FragmentLikesBinding>() {

    override fun getViewBinding(): FragmentLikesBinding {
        return FragmentLikesBinding.inflate(layoutInflater)
    }
}