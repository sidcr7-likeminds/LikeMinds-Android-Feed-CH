package com.likeminds.feedsx.feed.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsx.databinding.FragmentLikesBinding
import com.likeminds.feedsx.feed.view.LikesActivity.Companion.LIKES_SCREEN_EXTRAS
import com.likeminds.feedsx.feed.view.adapter.LikesScreenAdapter
import com.likeminds.feedsx.feed.view.model.LikeViewData
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesFragment : BaseFragment<FragmentLikesBinding>() {

    companion object {
        private const val TAG = "Likes Screen"
    }

    private lateinit var mLikesScreenAdapter: LikesScreenAdapter

    private lateinit var likesScreenExtras: LikesScreenExtras

    override fun getViewBinding(): FragmentLikesBinding {
        return FragmentLikesBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mLikesScreenAdapter = LikesScreenAdapter()
        binding.rvLikes.apply {
            layoutManager = linearLayoutManager
            adapter = mLikesScreenAdapter
            show()
        }

        attachPagination(
            binding.rvLikes,
            linearLayoutManager
        )

        //TODO: Testing data
        mLikesScreenAdapter.add(
            LikeViewData.Builder()
                .id("1")
                .user(UserViewData.Builder().name("Sid").customTitle("Admin").build())
                .build()
        )

        mLikesScreenAdapter.add(
            LikeViewData.Builder()
                .id("2")
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .build()
        )

        mLikesScreenAdapter.add(
            LikeViewData.Builder()
                .id("3")
                .user(UserViewData.Builder().name("Siddharth").build())
                .build()
        )
    }

    //attach scroll listener for pagination
    private fun attachPagination(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                // TODO: add logic
            }
        })
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(LIKES_SCREEN_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        likesScreenExtras = arguments?.getParcelable(LIKES_SCREEN_EXTRAS)!!
    }
}