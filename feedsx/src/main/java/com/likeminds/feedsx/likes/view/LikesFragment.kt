package com.likeminds.feedsx.likes.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.FragmentLikesBinding
import com.likeminds.feedsx.likes.adapter.LikesScreenAdapter
import com.likeminds.feedsx.likes.model.LikesScreenExtras
import com.likeminds.feedsx.likes.viewmodel.LikesViewModel
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment

class LikesFragment : BaseFragment<FragmentLikesBinding, LikesViewModel>() {

    companion object {
        private const val TAG = "Likes Screen"
    }

    private lateinit var mLikesScreenAdapter: LikesScreenAdapter

    private lateinit var likesScreenExtras: LikesScreenExtras

    override fun getViewModelClass(): Class<LikesViewModel> {
        return LikesViewModel::class.java
    }

    override fun getViewBinding(): FragmentLikesBinding {
        return FragmentLikesBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().likesComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(LikesActivity.LIKES_SCREEN_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        likesScreenExtras = arguments?.getParcelable(LikesActivity.LIKES_SCREEN_EXTRAS)!!
    }

    override fun setUpViews() {
        super.setUpViews()

        // sends like list open event
        viewModel.sendLikeListOpenEvent(
            likesScreenExtras.postId,
            likesScreenExtras.commentId
        )

        initData()
        initRecyclerView()
    }

    // observes data
    override fun observeData() {
        super.observeData()
        // observes likes api response
        viewModel.likesResponse.observe(viewLifecycleOwner) { response ->
            val listOfLikes = response.first
            val totalLikes = response.second

            mLikesScreenAdapter.addAll(listOfLikes)
            setTotalLikesCount(totalLikes)
        }

        // observes error message from likes api and shows toast with error message
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            ViewUtils.showErrorMessageToast(requireContext(), error)
            requireActivity().finish()
        }
    }

    // calls api for first page data to initialize rv
    private fun initData() {
        viewModel.getLikesData(
            likesScreenExtras.postId,
            likesScreenExtras.commentId,
            likesScreenExtras.entityType,
            1
        )
    }

    // setup recycler view
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mLikesScreenAdapter = LikesScreenAdapter()
        binding.rvLikes.apply {
            layoutManager = linearLayoutManager
            adapter = mLikesScreenAdapter
            show()
        }

        attachScrollListener(
            binding.rvLikes,
            linearLayoutManager
        )
    }

    // attach scroll listener for pagination
    private fun attachScrollListener(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (currentPage > 0) {
                    // calls api for paginated data
                    viewModel.getLikesData(
                        likesScreenExtras.postId,
                        likesScreenExtras.commentId,
                        likesScreenExtras.entityType,
                        currentPage
                    )
                }
            }
        })
    }

    // set total likes count on toolbar
    private fun setTotalLikesCount(totalLikes: Int) {
        val likesActivity = requireActivity() as LikesActivity
        likesActivity.binding.tvToolbarSubTitle.text =
            this.resources.getQuantityString(
                R.plurals.likes_small,
                totalLikes,
                totalLikes
            )
    }
}