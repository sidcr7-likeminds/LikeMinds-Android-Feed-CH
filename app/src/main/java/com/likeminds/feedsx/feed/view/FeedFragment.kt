package com.likeminds.feedsx.feed.view

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentFeedBinding
import com.likeminds.feedsx.feed.view.adapter.FeedPagerAdapter
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment : BaseFragment<FragmentFeedBinding>() {

    lateinit var pagerAdapter: FeedPagerAdapter

    override fun getViewBinding(): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initUI()
    }

    private fun initUI() {
        //TODO: Set as per branding
        binding.isBrandingBasic = true

        binding.viewPager.apply {
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.all_posts)
                1 -> getString(R.string.following)
                else -> throw IndexOutOfBoundsException()
            }
        }.attach()
    }
}