package com.likeminds.feedsx.post.detail.view

import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.feed.view.LikesActivity
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment

class PostDetailFragment :
    BaseFragment<FragmentPostDetailBinding>(),
    PostAdapterListener {

    private lateinit var postDetailExtras: PostDetailExtras

    private lateinit var mPostDetailAdapter: PostDetailAdapter

    override fun getViewBinding(): FragmentPostDetailBinding {
        return FragmentPostDetailBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mPostDetailAdapter = PostDetailAdapter(this)
        binding.rvPostDetails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mPostDetailAdapter
            show()
        }
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(POST_DETAIL_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        postDetailExtras = arguments?.getParcelable(POST_DETAIL_EXTRAS)!!
    }

    override fun updateSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        TODO("Not yet implemented")
    }

    override fun comment(postData: PostViewData) {
        TODO("Not yet implemented")
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        TODO("Not yet implemented")
    }

    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        TODO("Not yet implemented")
    }

    override fun showLikesScreen(postData: PostViewData) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postData.id)
            .likesCount(postData.likesCount)
            .build()
        LikesActivity.start(requireContext(), likesScreenExtras)
    }

    override fun postDetails(postData: PostViewData) {
        TODO("Not yet implemented")
    }
}