package com.likeminds.feedsx.post.detail.view

import android.util.Log
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.feed.view.LikesActivity
import com.likeminds.feedsx.feed.model.LikesScreenExtras
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment

class PostDetailFragment :
    BaseFragment<FragmentPostDetailBinding>(),
    PostAdapterListener,
    PostDetailAdapterListener,
    PostDetailReplyAdapterListener {

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
        mPostDetailAdapter = PostDetailAdapter(this, this, this)
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
        val text =
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
            CommentsCountViewData.Builder()
                .commentsCount(3)
                .build()
        )

        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(false)
                .id("1")
                .user(
                    UserViewData.Builder()
                        .name("Siddharth Dubey")
                        .build()
                )
                .text("This is a test comment 1")
                .build()
        )

        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(true)
                .id("2")
                .user(
                    UserViewData.Builder()
                        .name("Ishaan Jain")
                        .build()
                )
                .likesCount(10)
                .text("This is a test comment 2")
                .build()
        )
        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(false)
                .id("3")
                .user(
                    UserViewData.Builder()
                        .name("Natesh Rehlan")
                        .build()
                )
                .likesCount(10)
                .repliesCount(5)
                .text("This is a test comment 3")
                .build()
        )
        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(false)
                .id("4")
                .user(
                    UserViewData.Builder()
                        .name("Natesh Rehlan")
                        .build()
                )
                .likesCount(10)
                .repliesCount(5)
                .text("This is a test comment 4")
                .build()
        )
        mPostDetailAdapter.add(
            CommentViewData.Builder()
                .isLiked(false)
                .id("5")
                .user(
                    UserViewData.Builder()
                        .name("Natesh Rehlan")
                        .build()
                )
                .likesCount(10)
                .repliesCount(5)
                .text("This is a test comment 5")
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
        //TODO: menu item handle
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

    override fun likeComment(commentId: String) {
        // TODO: likes the comment with comment id
    }

    override fun fetchReplies(commentId: String, commentPosition: Int) {
        // TODO: fetch replies of the clicked comment and edit this dummy data
        if (mPostDetailAdapter[commentPosition] is CommentViewData) {
            val comment = mPostDetailAdapter[commentPosition] as CommentViewData
            comment.replies.addAll(
                mutableListOf(
                    CommentViewData.Builder()
                        .isLiked(false)
                        .id("6")
                        .user(
                            UserViewData.Builder()
                                .name("Natesh Rehlan")
                                .build()
                        )
                        .level(1)
                        .text("This is a test reply 1")
                        .build(),
                    CommentViewData.Builder()
                        .isLiked(false)
                        .id("7")
                        .user(
                            UserViewData.Builder()
                                .name("Natesh Rehlan")
                                .build()
                        )
                        .likesCount(10)
                        .repliesCount(5)
                        .level(1)
                        .text("This is a test reply 2")
                        .build(),
                    CommentViewData.Builder()
                        .isLiked(true)
                        .id("8")
                        .user(
                            UserViewData.Builder()
                                .name("Natesh Rehlan")
                                .build()
                        )
                        .likesCount(10)
                        .level(1)
                        .text("This is a test reply 3")
                        .build()
                )
            )
            mPostDetailAdapter.update(commentPosition, comment)
        }
    }

    override fun replyOnComment(commentId: String) {
        // TODO: fetch replies of the clicked comment
    }

    override fun onCommentMenuItemClicked(commentId: String, title: String) {
        //TODO: comment menu item
    }
}