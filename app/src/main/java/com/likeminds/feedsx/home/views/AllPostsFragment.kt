package com.likeminds.feedsx.home.views

import com.likeminds.feedsx.databinding.FragmentAllPostsBinding
import com.likeminds.feedsx.utils.customview.BaseFragment

class AllPostsFragment : BaseFragment<FragmentAllPostsBinding>() {

    companion object {
        //TODO: Bundles
        fun getInstance(): AllPostsFragment {
            return AllPostsFragment()
        }
    }

    override fun getViewBinding(): FragmentAllPostsBinding {
        return FragmentAllPostsBinding.inflate(layoutInflater)
    }
}