package com.likeminds.feedsx.post.detail.view

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.feed.view.LikesActivity
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment

class PostDetailFragment :
    BaseFragment<FragmentPostDetailBinding>(),
    PostAdapterListener,
    PostDetailAdapterListener {

    private lateinit var postDetailExtras: PostDetailExtras

    private lateinit var mPostDetailAdapter: PostDetailAdapter

    override fun getViewBinding(): FragmentPostDetailBinding {
        return FragmentPostDetailBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initRecyclerView()
        initCommentEditText()
    }

    private fun initRecyclerView() {
        mPostDetailAdapter = PostDetailAdapter(this, this)
        binding.rvPostDetails.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mPostDetailAdapter
            show()
        }
        addTestingData()
    }

    // TODO: handle hiding keyboard properly
    private fun initCommentEditText() {

        binding.etComment.apply {
            if (postDetailExtras.isEditTextFocused) focusAndShowKeyboard()

            doAfterTextChanged {
                if (it?.trim().isNullOrEmpty()) {
                    binding.ivCommentSend.apply {
                        isClickable = false
                        setImageResource(R.drawable.ic_comment_send_disable)
                    }
                } else {
                    binding.ivCommentSend.apply {
                        isClickable = true
                        setImageResource(R.drawable.ic_comment_send_enable)
                    }
                }
            }
        }
    }

    // TODO: testing data
    private fun addTestingData() {
        var text =
            "My name is Siddharth Dubey ajksfbajshdbfjakshdfvajhskdfv kahsgdv hsdafkgv ahskdfgv b "
        mPostDetailAdapter.add(
            PostViewData.Builder()
                .attachments(
                    listOf(
                        AttachmentViewData.Builder()
                            .attachmentType(DOCUMENT)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .build()
                            ).build(),
                        AttachmentViewData.Builder()
                            .attachmentType(DOCUMENT)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .build()
                            ).build(),
                        AttachmentViewData.Builder()
                            .attachmentType(DOCUMENT)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .build()
                            ).build(),
                        AttachmentViewData.Builder()
                            .attachmentType(DOCUMENT)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .build()
                            ).build(),
                        AttachmentViewData.Builder()
                            .attachmentType(DOCUMENT)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .build()
                            ).build()
                    )
                )
                .id("4")
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )

        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(true)
                .id("1")
                .text("This is a test comment 1")
                .build()
        )

        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(true)
                .id("2")
                .text("This is a test comment 2")
                .build()
        )
        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(false)
                .id("3")
                .text("This is a test comment 2")
                .build()
        )
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
        val item = mPostDetailAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .build()
            mPostDetailAdapter.update(position, newViewData)
        }
    }

    override fun comment(postData: PostViewData) {
        binding.etComment.focusAndShowKeyboard()
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        TODO("Not yet implemented")
    }

    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        if (position == mPostDetailAdapter.items().size - 1) {
            binding.rvPostDetails.post {
                scrollToPositionWithOffset(position)
            }
        }

        mPostDetailAdapter.update(
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
        (binding.rvPostDetails.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
            position,
            px
        )
    }

    override fun showLikesScreen(postData: PostViewData) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postData.id)
            .likesCount(postData.likesCount)
            .build()
        LikesActivity.start(requireContext(), likesScreenExtras)
    }
}