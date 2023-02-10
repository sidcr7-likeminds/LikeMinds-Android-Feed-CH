package com.likeminds.feedsx.post.detail.view

import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.feed.view.LikesActivity
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment

class PostDetailFragment :
    BaseFragment<FragmentPostDetailBinding>(),
    PostAdapterListener,
    PostDetailAdapterListener,
    PostDetailReplyAdapterListener {

    private lateinit var postDetailExtras: PostDetailExtras

    private lateinit var mPostDetailAdapter: PostDetailAdapter

    private var parentCommentIdToReply: String? = null

    companion object {
        const val REPLIES_THRESHOLD = 3
    }

    override fun getViewBinding(): FragmentPostDetailBinding {
        return FragmentPostDetailBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initRecyclerView()
        initCommentEditText()
        initListeners()

        //TODO: testing data
        updateCommentsCount(10)
    }

    // initializes the post detail screen recycler view
    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mPostDetailAdapter = PostDetailAdapter(this, this, this)
        binding.rvPostDetails.apply {
            layoutManager = linearLayoutManager
            adapter = mPostDetailAdapter
            show()
        }

        attachPagination(
            binding.rvPostDetails,
            linearLayoutManager
        )
        addTestingData()
    }

    // TODO: call after fetching post
    // updates the comments count on toolbar
    private fun updateCommentsCount(commentsCount: Int) {
        (requireActivity() as PostDetailActivity).binding.tvToolbarSubTitle.text =
            this.resources.getQuantityString(
                R.plurals.comments_small,
                commentsCount,
                commentsCount
            )
    }

    // attach scroll listener for pagination for comments
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

    // initializes comment edittext with TextWatcher and focuses the keyboard
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

    private fun initListeners() {
        binding.apply {
            ivCommentSend.setOnClickListener {
                if (parentCommentIdToReply != null) {
                    // input text is reply to a comment
                    // TODO: create a reply to comment
                } else {
                    // input text is a comment
                    // TODO: create a new comment
                }
            }

            ivRemoveReplyingTo.setOnClickListener {
                parentCommentIdToReply = null
                tvReplyingTo.hide()
                ivRemoveReplyingTo.hide()
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
                .likesCount(100)
                .repliesCount(10)
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

    // updates post view data when see more/see less is clicked
    override fun updateSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        val item = mPostDetailAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .build()
            mPostDetailAdapter.update(position, newViewData)
        }
    }

    // callback when add comment is clicked on post
    override fun comment(postId: String) {
        binding.etComment.focusAndShowKeyboard()
    }

    // callback for post menu item click
    override fun onPostMenuItemClicked(postId: String, title: String) {
        //TODO: menu item handle
    }

    // callback when +x more text is clicked to see more documents
    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        if (position == mPostDetailAdapter.items().size - 1) {
            binding.rvPostDetails.post {
                scrollToPositionWithOffset(position)
            }
        }

        mPostDetailAdapter.update(
            position,
            postData.toBuilder().isExpanded(true).build()
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

    // callback when likes count is clicked - opens likes screen
    override fun showLikesScreen(postData: PostViewData) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postData.id)
            .likesCount(postData.likesCount)
            .build()
        LikesActivity.start(requireContext(), likesScreenExtras)
    }

    // callback when post is liked
    override fun likeComment(commentId: String) {
        // TODO: likes the comment with comment id
    }

    // callback when replies count is clicked - fetches replies for the comment
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
            binding.rvPostDetails.smoothScrollToPosition(
                mPostDetailAdapter.itemCount
            )
        }
    }

    // callback when replying on a comment
    override fun replyOnComment(
        commentId: String,
        commentPosition: Int,
        parentCommenter: UserViewData
    ) {
        // TODO: fetch replies of the clicked comment
        parentCommentIdToReply = commentId
        binding.apply {
            tvReplyingTo.show()
            ivRemoveReplyingTo.show()

            tvReplyingTo.text = String.format(
                getString(R.string.replying_to_s),
                parentCommenter.name
            )

            etComment.focusAndShowKeyboard()

            rvPostDetails.smoothScrollToPosition(
                commentPosition
            )
        }
    }

    // callback for comment's menu is item
    override fun onCommentMenuItemClicked(commentId: String, title: String) {
        //TODO: comment menu item
    }

    // callback when view more replies is clicked
    override fun viewMoreReplies(
        parentCommentId: String,
        parentCommentPosition: Int,
        currentVisibleReplies: Int
    ) {
        // TODO: fetch comment replies. Testing data. Fetch min of REPLIES_THRESHOLD or remaining replies

        if (mPostDetailAdapter[parentCommentPosition] is CommentViewData) {
            val comment = mPostDetailAdapter[parentCommentPosition] as CommentViewData
            if (comment.replies.size < comment.repliesCount) {
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
            }
            mPostDetailAdapter.update(parentCommentPosition, comment)
        }
    }

    // callback when the item of reply menu is clicked
    override fun onReplyMenuItemClicked(replyId: String, title: String) {
        //TODO: handle menu item click for replies.
    }
}