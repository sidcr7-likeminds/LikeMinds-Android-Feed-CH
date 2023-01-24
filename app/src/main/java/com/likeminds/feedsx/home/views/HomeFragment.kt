package com.likeminds.feedsx.home.views

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentHomeBinding
import com.likeminds.feedsx.home.adapter.HomePagerAdapter
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    lateinit var pagerAdapter: HomePagerAdapter

    override fun getViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initUI()
    }

    private fun initUI() {
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