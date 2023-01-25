package com.likeminds.feedsx.feed.view.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.likeminds.feedsx.feed.view.UniversalFeedFragment
import com.likeminds.feedsx.feed.view.FollowingFeedFragment
import com.likeminds.feedsx.feed.view.FeedFragment

class FeedPagerAdapter constructor(
    private val fragment: FeedFragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UniversalFeedFragment.getInstance()
            1 -> FollowingFeedFragment.getInstance()
            else -> throw IndexOutOfBoundsException()
        }
    }
}