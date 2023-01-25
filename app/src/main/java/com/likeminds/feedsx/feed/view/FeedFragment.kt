package com.likeminds.feedsx.feed.view

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.databinding.FragmentFeedBinding
import com.likeminds.feedsx.feed.viewmodel.FeedViewModel
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment :
    BaseFragment<FragmentFeedBinding>(),
    PostAdapterListener {

    private val viewModel: FeedViewModel by viewModels()
    lateinit var mPostAdapter: PostAdapter

    override fun getViewBinding(): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initUI()
        initToolbar()
    }

    private fun initUI() {
        //TODO: Set as per branding
        binding.isBrandingBasic = true

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mPostAdapter = PostAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mPostAdapter
            show()
        }
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        //if user is guest user hide, profile icon from toolbar
        binding.memberImage.isVisible = !isGuestUser

        //click listener -> open profile screen
        binding.memberImage.setOnClickListener {
            TODO("Not yet implemented")
        }

        binding.ivSearch.setOnClickListener {
            TODO("Not yet implemented")
        }
    }

    override fun updateSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .build()
            if (newViewData != null) {
                mPostAdapter.update(position, newViewData)
            }
        }
    }

    override fun pinPost() {
        TODO("Not yet implemented")
    }

    override fun savePost() {
        TODO("Not yet implemented")
    }

    override fun likePost() {
        TODO("Not yet implemented")
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        //TODO: Perform action on post's menu item selection
        Toast.makeText(context, "Post id :${postId}, Title :${title}", Toast.LENGTH_SHORT)
    }

    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        if (position == mPostAdapter.items().size - 1) {
            binding.recyclerView.post {
                scrollToPositionWithOffset(position)
            }
        }

        mPostAdapter.update(
            position, postData.toBuilder().isExpanded(true).build()
        )
    }

    /**
     * Scroll to a position with offset from the top header
     * @param position Index of the item to scroll to
     */
    private fun scrollToPositionWithOffset(position: Int) {
        val px = if (binding.vTopBackground.height == 0) {
            (ViewUtils.dpToPx(75) * 1.5).toInt()
        } else {
            (binding.vTopBackground.height * 1.5).toInt()
        }
        (binding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
            position,
            px
        )
    }
}