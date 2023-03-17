package com.likeminds.feedsx.post.detail.view

import android.app.Activity
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.delete.model.DELETE_TYPE_COMMENT
import com.likeminds.feedsx.delete.model.DELETE_TYPE_POST
import com.likeminds.feedsx.delete.model.DeleteExtras
import com.likeminds.feedsx.delete.view.DeleteAlertDialogFragment
import com.likeminds.feedsx.delete.view.DeleteDialogFragment
import com.likeminds.feedsx.feed.model.COMMENT
import com.likeminds.feedsx.feed.model.LikesScreenExtras
import com.likeminds.feedsx.feed.model.POST
import com.likeminds.feedsx.feed.view.LikesActivity
import com.likeminds.feedsx.overflowmenu.model.*
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.report.model.REPORT_TYPE_COMMENT
import com.likeminds.feedsx.report.model.REPORT_TYPE_POST
import com.likeminds.feedsx.report.model.ReportExtras
import com.likeminds.feedsx.report.model.ReportType
import com.likeminds.feedsx.report.view.ReportActivity
import com.likeminds.feedsx.report.view.ReportSuccessDialog
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.ViewUtils.showShortToast
import com.likeminds.feedsx.utils.customview.BaseFragment

class PostDetailFragment :
    BaseFragment<FragmentPostDetailBinding>(),
    PostAdapterListener,
    PostDetailAdapterListener,
    PostDetailReplyAdapterListener,
    DeleteAlertDialogFragment.DeleteAlertDialogListener,
    DeleteDialogFragment.DeleteDialogListener {

    private lateinit var postDetailExtras: PostDetailExtras

    private lateinit var mPostDetailAdapter: PostDetailAdapter
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

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
        initSwipeRefreshLayout()
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
            if (itemAnimator is SimpleItemAnimator)
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            show()
        }

        attachScrollListener(
            binding.rvPostDetails,
            linearLayoutManager
        )
        addTestingData()
    }

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            BrandingData.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = true
            fetchRefreshedData()
        }
    }

    //TODO: Call api and refresh the post data
    private fun fetchRefreshedData() {
        //TODO: testing data
        mPostDetailAdapter.add(
            2,
            CommentViewData.Builder()
                .isLiked(false)
                .id("6")
                .user(
                    UserViewData.Builder()
                        .name("Sid")
                        .build()
                )
                .likesCount(140)
                .text("This is a test comment 6")
                .build()
        )
        mPostDetailAdapter.add(
            3,
            CommentViewData.Builder()
                .isLiked(false)
                .id("7")
                .user(
                    UserViewData.Builder()
                        .name("Siddharth")
                        .build()
                )
                .likesCount(100)
                .isLiked(true)
                .text("This is a test comment 7")
                .build()
        )
        mSwipeRefreshLayout.isRefreshing = false
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
    private fun attachScrollListener(
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
        binding.apply {
            if (postDetailExtras.isEditTextFocused) etComment.focusAndShowKeyboard()

            etComment.doAfterTextChanged {
                if (it?.trim().isNullOrEmpty()) {
                    ivCommentSend.isClickable = false
                    ivCommentSend.setImageResource(R.drawable.ic_comment_send_disable)
                } else {
                    ivCommentSend.isClickable = true
                    ivCommentSend.setImageResource(R.drawable.ic_comment_send_enable)
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
                .id("63f4caadc52f148210f7496a")
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
                .id("6412b3b39b922f785f94da50")
                .postId("63f4caadc52f148210f7496a")
                .user(
                    UserViewData.Builder()
                        .name("Siddharth Dubey")
                        .build()
                )
                .menuItems(
                    listOf(
                        OverflowMenuItemViewData.Builder().title(DELETE_COMMENT_MENU_ITEM)
                            .entityId("1").build(),
                        OverflowMenuItemViewData.Builder().title(REPORT_COMMENT_MENU_ITEM)
                            .entityId("1").build()
                    )
                )
                .likesCount(100)
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
        // TODO: handle when opened from route
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(POST_DETAIL_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        postDetailExtras = arguments?.getParcelable(POST_DETAIL_EXTRAS)!!
    }

    // processes delete entity request
    private fun deleteEntity(
        entityId: String,
        @ReportType
        entityType: Int
    ) {
        //TODO: set isAdmin
        val isAdmin = false
        val deleteExtras = DeleteExtras.Builder()
            .entityId(entityId)
            .entityType(entityType)
            .build()
        if (isAdmin) {
            // when CM deletes other user's post
            DeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        } else {
            // when user deletes their own entity
            DeleteAlertDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        }
    }

    // TODO testing
    // Processes report action on entity
    private fun reportEntity(
        entityId: String,
        creatorId: String,
        @ReportType
        entityType: Int
    ) {
        //create extras for [ReportActivity]
        val reportExtras = ReportExtras.Builder()
            .entityId(entityId)
            .entityType(entityType)
            .entityCreatorId(creatorId)
            .build()

        //get Intent for [ReportActivity]
        val intent = ReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
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

    // callback when likes count of post is clicked - opens likes screen
    override fun showLikesScreen(postId: String) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postId)
            .entityType(POST)
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

    override fun onPostMenuItemClicked(
        postId: String,
        title: String,
        creatorId: String
    ) {
        when (title) {
            DELETE_POST_MENU_ITEM -> {
                deleteEntity(postId, REPORT_TYPE_POST)
            }
            REPORT_POST_MENU_ITEM -> {
                reportEntity(postId, creatorId, REPORT_TYPE_POST)
            }
            PIN_POST_MENU_ITEM -> {
                // TODO: pin post
            }
            UNPIN_POST_MENU_ITEM -> {
                // TODO: unpin post
            }
        }
    }

    // callback for comment's menu is item
    override fun onCommentMenuItemClicked(
        commentId: String,
        title: String,
        creatorId: String
    ) {
        when (title) {
            DELETE_COMMENT_MENU_ITEM -> {
                deleteEntity(commentId, REPORT_TYPE_COMMENT)
            }
            REPORT_COMMENT_MENU_ITEM -> {
                reportEntity(commentId, creatorId, REPORT_TYPE_COMMENT)
            }
        }
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

    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                ReportSuccessDialog("Message").show(
                    childFragmentManager,
                    ReportSuccessDialog.TAG
                )
            }
        }

    // callback when self post is deleted by user
    override fun delete(deleteExtras: DeleteExtras) {
        // TODO: delete post/comment by user
        Log.d("TAG", "initializeListeners: ${deleteExtras.entityType}")
        when (deleteExtras.entityType) {
            DELETE_TYPE_POST -> showShortToast(
                requireContext(),
                getString(R.string.post_deleted)
            )
            DELETE_TYPE_COMMENT -> showShortToast(
                requireContext(),
                getString(R.string.comment_deleted)
            )
        }
    }

    // callback when other's post is deleted by CM
    override fun delete(deleteExtras: DeleteExtras, reportTagId: String, reason: String) {
        // TODO: delete post/comment by admin
        Log.d("TAG", "initializeListeners by admin: ${deleteExtras.entityType}")
        when (deleteExtras.entityType) {
            DELETE_TYPE_POST -> showShortToast(
                requireContext(),
                getString(R.string.post_deleted)
            )
            DELETE_TYPE_COMMENT -> showShortToast(
                requireContext(),
                getString(R.string.comment_deleted)
            )
        }
    }

    // callback when likes count of a comment is clicked - opens likes screen
    override fun showLikesScreen(postId: String, commentId: String) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postId)
            .commentId(commentId)
            .entityType(COMMENT)
            .build()
        LikesActivity.start(requireContext(), likesScreenExtras)
    }
}