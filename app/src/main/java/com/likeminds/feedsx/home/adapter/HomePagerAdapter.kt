package com.likeminds.feedsx.home.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.likeminds.feedsx.home.views.AllPostsFragment
import com.likeminds.feedsx.home.views.FollowingFragment
import com.likeminds.feedsx.home.views.HomeFragment
import javax.inject.Inject

class HomePagerAdapter constructor(
    private val fragment: HomeFragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AllPostsFragment.getInstance()
            1 -> FollowingFragment.getInstance()
            else -> throw IndexOutOfBoundsException()
        }
    }
}