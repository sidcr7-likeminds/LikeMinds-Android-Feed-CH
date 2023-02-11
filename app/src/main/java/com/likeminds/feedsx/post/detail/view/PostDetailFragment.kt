package com.likeminds.feedsx.post.detail.view

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.deleteentity.model.DeleteEntityExtras
import com.likeminds.feedsx.deleteentity.view.DeleteEntityDialogFragment
import com.likeminds.feedsx.feed.model.LikesScreenExtras
import com.likeminds.feedsx.feed.view.LikesActivity
import com.likeminds.feedsx.overflowmenu.model.*
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.report.model.REPORT_TYPE_COMMENT
import com.likeminds.feedsx.report.model.REPORT_TYPE_POST
import com.likeminds.feedsx.report.model.ReportExtras
import com.likeminds.feedsx.report.model.ReportType
import com.likeminds.feedsx.report.view.ReportActivity
import com.likeminds.feedsx.report.view.ReportSuccessDialog
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
    private lateinit var alertDialog: AlertDialog

    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                ReportSuccessDialog("Message").show(
                    childFragmentManager,
                    ReportSuccessDialog.TAG
                )
            }
        }

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
                .menuItems(
                    listOf(
                        OverflowMenuItemViewData.Builder().title(DELETE_COMMENT_MENU_ITEM).entityId("1").build(),
                        OverflowMenuItemViewData.Builder().title(REPORT_COMMENT_MENU_ITEM).entityId("1").build()
                    )
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

    // processes delete entity request
    private fun deleteEntity(
        entityId: String,
        @ReportType
        entityType: Int
    ) {
        //TODO: set isAdmin
        val isAdmin = true
        if (isAdmin) {
            val deleteEntityExtras = DeleteEntityExtras.Builder()
                .entityId(entityId)
                .entityType(entityType)
                .build()
            DeleteEntityDialogFragment.showDialog(
                childFragmentManager,
                deleteEntityExtras
            )
        } else {
            showDeleteEntityDialog(entityType)
        }
    }

    // shows delete entity dialog when user deletes their own entity (post/comment)
    private fun showDeleteEntityDialog(
        @ReportType
        entityType: Int
    ) {
        val builder = AlertDialog.Builder(requireContext())
        var message = getString(R.string.delete_post_message)
        var title = getString(R.string.delete_post_question)
        if (entityType == REPORT_TYPE_COMMENT) {
            message = getString(R.string.delete_comment_message)
            title = getString(R.string.delete_comment_question)
        }
        builder.setMessage(message)
            .setTitle(title)
            .setCancelable(true)
            .setPositiveButton(getString(R.string.delete_caps)) { _, _ ->

            }.setNegativeButton(getString(R.string.cancel_caps)) { _, _ ->
                alertDialog.dismiss()
            }
        //Creating dialog box
        alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_40))
    }

    // Processes report action on entity
    private fun reportEntity(
        entityId: String,
        @ReportType
        entityType: Int
    ) {
        //create extras for [ReportActivity]
        val reportExtras = ReportExtras.Builder()
            .entityId(entityId)
            .type(entityType)
            .build()

        //get Intent for [ReportActivity]
        val intent = ReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
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

    override fun onPostMenuItemClicked(postId: String, title: String) {
        when (title) {
            DELETE_POST_MENU_ITEM -> {
                deleteEntity(postId, REPORT_TYPE_POST)
            }
            REPORT_POST_MENU_ITEM -> {
                reportEntity(postId, REPORT_TYPE_POST)
            }
            PIN_POST_MENU_ITEM -> {
                // TODO: pin post
            }
            UNPIN_POST_MENU_ITEM -> {
                // TODO: unpin post
            }
        }
    }

    override fun onCommentMenuItemClicked(commentId: String, title: String) {
        when (title) {
            DELETE_COMMENT_MENU_ITEM -> {
                deleteEntity(commentId, REPORT_TYPE_COMMENT)
            }
            REPORT_COMMENT_MENU_ITEM -> {
                reportEntity(commentId, REPORT_TYPE_COMMENT)
            }
        }
    }
}