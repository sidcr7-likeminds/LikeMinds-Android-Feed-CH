package com.likeminds.feedsx.post.detail.view

import android.app.Activity
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feedsx.InitiateViewModel
import com.likeminds.feedsx.LMAnalytics
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.databinding.FragmentPostDetailBinding
import com.likeminds.feedsx.delete.model.*
import com.likeminds.feedsx.delete.view.AdminDeleteDialogFragment
import com.likeminds.feedsx.delete.view.SelfDeleteDialogFragment
import com.likeminds.feedsx.feed.util.PostEvent
import com.likeminds.feedsx.likes.model.COMMENT
import com.likeminds.feedsx.likes.model.LikesScreenExtras
import com.likeminds.feedsx.likes.model.POST
import com.likeminds.feedsx.likes.view.LikesActivity
import com.likeminds.feedsx.overflowmenu.model.*
import com.likeminds.feedsx.post.detail.model.CommentsCountViewData
import com.likeminds.feedsx.post.detail.model.NoCommentsViewData
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity.Companion.POST_DETAIL_EXTRAS
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter.PostDetailReplyAdapterListener
import com.likeminds.feedsx.post.detail.viewmodel.PostDetailViewModel
import com.likeminds.feedsx.post.viewmodel.PostActionsViewModel
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.report.model.*
import com.likeminds.feedsx.report.view.ReportActivity
import com.likeminds.feedsx.report.view.ReportFragment
import com.likeminds.feedsx.report.view.ReportSuccessDialog
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.feedsx.utils.membertagging.view.MemberTaggingView
import com.likeminds.feedsx.utils.model.BaseViewType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class PostDetailFragment :
    BaseFragment<FragmentPostDetailBinding>(),
    PostAdapterListener,
    PostDetailAdapterListener,
    PostDetailReplyAdapterListener,
    SelfDeleteDialogFragment.DeleteAlertDialogListener,
    AdminDeleteDialogFragment.DeleteDialogListener {

    private val viewModel: PostDetailViewModel by viewModels()

    // shared viewModel between [FeedFragment] and [PostDetailFragment] for postActions
    private val postActionsViewModel: PostActionsViewModel by activityViewModels()

    private val initiateViewModel: InitiateViewModel by activityViewModels()

    private lateinit var postDetailExtras: PostDetailExtras

    private lateinit var mPostDetailAdapter: PostDetailAdapter
    private lateinit var mScrollListener: EndlessRecyclerScrollListener
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout

    private var parentCommentIdToReply: String? = null
    private var toFindComment: Boolean = false

    private lateinit var memberTagging: MemberTaggingView

    // fixed position of viewTypes in adapter
    private val postDataPosition = 0
    private val commentsCountPosition = 1
    private val commentsStartPosition = 2

    // [postPublisher] to publish changes in the post
    private val postEvent = PostEvent.getPublisher()

    companion object {
        const val TAG = "PostDetailFragment"
        const val REPLIES_THRESHOLD = 5
    }

    override fun getViewBinding(): FragmentPostDetailBinding {
        return FragmentPostDetailBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(POST_DETAIL_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        postDetailExtras =
            arguments?.getParcelable(POST_DETAIL_EXTRAS)
                ?: throw emptyExtrasException(TAG)
        // TODO: route for replies
//        checkForComments()
    }

    //to check for source of the follow trigger
    private fun checkForComments() {
        //if extras contains commentId: redirect to comment
        if (!postDetailExtras.commentId.isNullOrEmpty()) {
            toFindComment = true
        }
    }

    override fun setUpViews() {
        super.setUpViews()

        binding.buttonColor = LMBranding.getButtonsColor()
        fetchPostData()
        initRecyclerView()
        initMemberTaggingView()
        initSwipeRefreshLayout()
        initCommentEditText()
        initListeners()
    }

    // fetches post data to set initial data
    private fun fetchPostData() {
        // show progress bar
        ProgressHelper.showProgress(binding.progressBar)
        //if source is notification, then call initiate first and then other apis
        if (postDetailExtras.source == LMAnalytics.Source.NOTIFICATION) {
            initiateViewModel.initiateUser()
        } else {
            viewModel.getPost(postDetailExtras.postId, 1)
        }
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
    }

    /**
     * initializes the [memberTaggingView] with the edit text
     * also sets listener to the [memberTaggingView]
     */
    private fun initMemberTaggingView() {
        memberTagging = binding.memberTaggingView
        memberTagging.initialize(
            MemberTaggingExtras.Builder()
                .editText(binding.etComment)
                .maxHeightInPercentage(0.4f)
                .color(
                    LMBranding.getTextLinkColor()
                )
                .build()
        )
        memberTagging.addListener(object : MemberTaggingViewListener {
            override fun callApi(page: Int, searchName: String) {
                viewModel.getMembersForTagging(page, searchName)
            }
        })
    }

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            LMBranding.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            refreshPostData()
        }
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

    // initializes text-watcher and click listeners
    private fun initListeners() {
        binding.apply {
            ivCommentSend.setOnClickListener {
                val text = etComment.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                val postId = postDetailExtras.postId
                if (parentCommentIdToReply != null) {
                    // input text is reply to a comment
                    val parentCommentId = parentCommentIdToReply ?: return@setOnClickListener
                    val parentComment = getIndexAndCommentFromAdapter(parentCommentId)?.second
                        ?: return@setOnClickListener
                    viewModel.replyComment(
                        parentComment.userId,
                        postDetailExtras.postId,
                        parentCommentId,
                        updatedText
                    )
                    hideReplyingToView()
                } else {
                    // input text is a comment
                    viewModel.addComment(postId, updatedText)
                }
                ViewUtils.hideKeyboard(this.root)
                etComment.text = null
            }
            ivCommentSend.isClickable = false
            ivCommentSend.setImageResource(R.drawable.ic_comment_send_disable)

            ivRemoveReplyingTo.setOnClickListener {
                hideReplyingToView()
            }
        }
    }

    override fun observeData() {
        super.observeData()
        observeInitiateResponse()
        observePostData()
        observeCommentData()
        observeMembersTaggingList()
        observeErrors()
    }

    private fun observeInitiateResponse() {
        initiateViewModel.userResponse.observe(viewLifecycleOwner) {
            //get post detail
            viewModel.getPost(postDetailExtras.postId, 1)
        }

        initiateViewModel.logoutResponse.observe(viewLifecycleOwner) {
            binding.apply {
                mainLayout.hide()
                invalidAccessLayout.show()
            }
        }

        initiateViewModel.initiateErrorMessage.observe(viewLifecycleOwner) {
            ProgressHelper.hideProgress(binding.progressBar)
            ViewUtils.showErrorMessageToast(requireContext(), it)
        }
    }

    // observes live data related to post
    private fun observePostData() {
        // observes postResponse live data
        viewModel.postResponse.observe(viewLifecycleOwner) { pair ->
            //hide progress bar
            ProgressHelper.hideProgress(binding.progressBar)
            binding.apply {
                mainLayout.show()
                invalidAccessLayout.hide()
            }
            //page in sent in api
            val page = pair.first

            // post data
            val post = pair.second

            // notifies the subscribers about the change in post data
            postEvent.notify(Pair(post.id, post))

            // update the comments count
            updateCommentsCount(post.commentsCount)

            //if pull to refresh is called
            if (mSwipeRefreshLayout.isRefreshing) {
                setPostDataAndScrollToTop(post)
                mSwipeRefreshLayout.isRefreshing = false
                return@observe
            }

            //normal adding
            if (page == 1) {
                setPostDataAndScrollToTop(post)
            } else {
                updatePostAndAddComments(post)
            }
        }

        // observes deletePostResponse LiveData
        postActionsViewModel.deletePostResponse.observe(viewLifecycleOwner) {
            // notifies the subscribers about the deletion of post
            postEvent.notify(Pair(postDetailExtras.postId, null))

            ViewUtils.showShortToast(
                requireContext(),
                getString(R.string.post_deleted)
            )
            requireActivity().finish()
        }

        // observes pinPostResponse LiveData
        postActionsViewModel.pinPostResponse.observe(viewLifecycleOwner) {
            val post = mPostDetailAdapter[postDataPosition] as PostViewData

            if (post.isPinned) {
                ViewUtils.showShortToast(requireContext(), getString(R.string.post_pinned_to_top))
            } else {
                ViewUtils.showShortToast(requireContext(), getString(R.string.post_unpinned))
            }
        }
    }

    private fun observeCommentData() {
        // observes addCommentResponse LiveData
        viewModel.addCommentResponse.observe(viewLifecycleOwner) { comment ->
            if (mPostDetailAdapter[commentsCountPosition] is NoCommentsViewData) {
                mPostDetailAdapter.removeIndex(commentsCountPosition)
            }
            // gets old [CommentsCountViewData] from adapter
            if (mPostDetailAdapter[commentsCountPosition] != null) {
                val oldCommentsCountViewData =
                    (mPostDetailAdapter[commentsCountPosition] as CommentsCountViewData)

                // updates old [CommentsCountViewData] by adding to [commentsCount]
                val updatedCommentsCountViewData = oldCommentsCountViewData.toBuilder()
                    .commentsCount(oldCommentsCountViewData.commentsCount + 1)
                    .build()

                // updates [CommentsCountViewData]
                mPostDetailAdapter.update(commentsCountPosition, updatedCommentsCountViewData)
            } else {
                // creates new [CommentsCountViewData] when the added comment is first
                val newCommentsCountViewData = CommentsCountViewData.Builder()
                    .commentsCount(1)
                    .build()
                mPostDetailAdapter.add(commentsCountPosition, newCommentsCountViewData)
            }

            // gets post from adapter
            var post = mPostDetailAdapter[postDataPosition] as PostViewData
            post = post.toBuilder()
                .commentsCount(post.commentsCount + 1)
                .build()

            // notifies the subscribers about the change in post data
            postEvent.notify(Pair(post.id, post))

            // updates comments count on header
            updateCommentsCount(post.commentsCount)

            // adds new comment to adapter
            mPostDetailAdapter.add(commentsStartPosition, comment)

            // scroll to comment's position
            scrollToPositionWithOffset(commentsStartPosition, 75)

            // updates comment data in post
            mPostDetailAdapter.update(postDataPosition, post)
        }

        // observes addReplyResponse LiveData
        viewModel.addReplyResponse.observe(viewLifecycleOwner) { pair ->
            // [parentCommentId] for the reply
            val parentCommentId = pair.first

            // view data of comment with level-1
            val replyViewData = pair.second

            // adds reply to the adapter
            addReplyToAdapter(parentCommentId, replyViewData)
        }

        // observes deleteCommentResponse LiveData
        viewModel.deleteCommentResponse.observe(viewLifecycleOwner) { pair ->
            val commentId = pair.first
            val parentCommentId = pair.second

            // level-0 comment
            if (parentCommentId == null) {
                removeDeletedComment(commentId)
            } else {
                // level-1 comment
                removeDeletedReply(parentCommentId, commentId)
            }
        }

        viewModel.getCommentResponse.observe(viewLifecycleOwner) { pair ->
            //page in api send
            val page = pair.first

            //comment data
            val comment = pair.second

            // adds paginated replies to adapter
            addReplies(comment, page)
        }
    }

    private fun removeDeletedComment(commentId: String) {
        // gets old [CommentsCountViewData] from adapter
        val oldCommentsCountViewData =
            (mPostDetailAdapter[commentsCountPosition] as CommentsCountViewData)

        // creates new [CommentsCountViewData] by adding to [commentsCount]
        val newCommentsCountViewData = oldCommentsCountViewData.toBuilder()
            .commentsCount(oldCommentsCountViewData.commentsCount - 1)
            .build()

        // updates [CommentsCountViewData]
        mPostDetailAdapter.update(commentsCountPosition, newCommentsCountViewData)

        // get the deleted comment from the adapter
        val indexToRemove =
            getIndexAndCommentFromAdapter(commentId)?.first ?: return
        // removes the deleted comment from the adapter
        mPostDetailAdapter.removeIndex(indexToRemove)
        ViewUtils.showShortToast(
            requireContext(),
            getString(R.string.comment_deleted)
        )

        if (newCommentsCountViewData.commentsCount == 0) {
            mPostDetailAdapter.removeIndex(commentsCountPosition)
            // adds no comments view data to adapter
            val noCommentViewData = NoCommentsViewData.Builder().build()
            mPostDetailAdapter.add(noCommentViewData)
        }
    }

    /**
     * Observes for member tagging list, This is a live observer which will update itself on addition of new members
     * [taggingData] contains first -> page called in api
     * second -> Community Members and Groups
     */
    private fun observeMembersTaggingList() {
        viewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    // observes error events
    private fun observeErrors() {
        viewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is PostDetailViewModel.ErrorMessageEvent.GetTaggingList -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is PostDetailViewModel.ErrorMessageEvent.GetPost -> {
                    mSwipeRefreshLayout.isRefreshing = false
                    ProgressHelper.hideProgress(binding.progressBar)
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is PostDetailViewModel.ErrorMessageEvent.LikeComment -> {
                    val commentId = response.commentId

                    //get comment and index
                    val pair = getIndexAndCommentFromAdapter(commentId) ?: return@onEach
                    val comment = pair.second
                    val index = pair.first

                    //update comment view data
                    val updatedComment = comment.toBuilder()
                        .isLiked(false)
                        .fromCommentLiked(true)
                        .likesCount(comment.likesCount - 1)
                        .build()

                    //update recycler view
                    mPostDetailAdapter.update(index, updatedComment)

                    //show error message
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is PostDetailViewModel.ErrorMessageEvent.AddComment -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is PostDetailViewModel.ErrorMessageEvent.DeleteComment -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is PostDetailViewModel.ErrorMessageEvent.GetComment -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
            }
        }

        postActionsViewModel.errorMessageEventFlow.onEach { response ->
            when (response) {
                is PostActionsViewModel.ErrorMessageEvent.LikePost -> {
                    //get post
                    val post = mPostDetailAdapter[postDataPosition] as PostViewData

                    //update post view data
                    val updatedPost = post.toBuilder()
                        .isLiked(false)
                        .fromPostLiked(true)
                        .likesCount(post.likesCount - 1)
                        .build()

                    postEvent.notify(Pair(updatedPost.id, updatedPost))

                    //update recycler view
                    mPostDetailAdapter.update(postDataPosition, updatedPost)

                    //show error message
                    val errorMessage = response.errorMessage
                    ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
                is PostActionsViewModel.ErrorMessageEvent.SavePost -> {
                    //get post
                    val post = mPostDetailAdapter[postDataPosition] as PostViewData

                    //update post view data
                    val updatedPost = post.toBuilder()
                        .isSaved(false)
                        .fromPostSaved(true)
                        .build()

                    postEvent.notify(Pair(updatedPost.id, updatedPost))

                    //update recycler view
                    mPostDetailAdapter.update(postDataPosition, updatedPost)

                    //show error message
                    val errorMessage = response.errorMessage
                    ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
                is PostActionsViewModel.ErrorMessageEvent.DeletePost -> {
                    val errorMessage = response.errorMessage
                    ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
                is PostActionsViewModel.ErrorMessageEvent.PinPost -> {
                    //get post
                    val post = mPostDetailAdapter[postDataPosition] as PostViewData

                    //update post view data
                    val updatedPost = post.toBuilder()
                        .isPinned(!post.isPinned)
                        .build()

                    //update recycler view
                    mPostDetailAdapter.update(postDataPosition, updatedPost)

                    //show error message
                    val errorMessage = response.errorMessage
                    ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
                }
            }
        }
    }


    /*
    * UI Block
    */


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
        mScrollListener = object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (currentPage > 0) {
                    viewModel.getPost(postDetailExtras.postId, currentPage)
                }
            }
        }
        recyclerView.addOnScrollListener(mScrollListener)
    }

    // callback when add comment is clicked on post
    override fun comment(postId: String) {
        binding.etComment.focusAndShowKeyboard()
    }

    // processes delete post request
    private fun deletePost(
        postId: String,
        creatorId: String
    ) {
        val deleteExtras = DeleteExtras.Builder()
            .postId(postId)
            .entityType(DELETE_TYPE_POST)
            .build()

        showDeleteDialog(creatorId, deleteExtras)
    }

    // processes delete comment request
    private fun deleteComment(
        postId: String,
        commentId: String,
        creatorId: String,
        parentCommentId: String? = null,
    ) {
        val deleteExtras = DeleteExtras.Builder()
            .postId(postId)
            .commentId(commentId)
            .entityType(DELETE_TYPE_COMMENT)
            .parentCommentId(parentCommentId)
            .build()

        showDeleteDialog(creatorId, deleteExtras)
    }

    private fun showDeleteDialog(creatorId: String, deleteExtras: DeleteExtras) {
        if (creatorId == postActionsViewModel.getUserUniqueId()) {
            // when user deletes their own entity
            SelfDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        } else {
            // when CM deletes other user's entity
            AdminDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        }
    }

    // handles visibility of no comments view
//    private fun handleNoCommentsView(isVisible: Boolean) {
//        binding.apply {
//            tvNoComment.isVisible = isVisible
//            tvBeFirst.isVisible = isVisible
//        }
//    }

    // hides the replying to view
    private fun hideReplyingToView() {
        binding.apply {
            parentCommentIdToReply = null
            tvReplyingTo.hide()
            ivRemoveReplyingTo.hide()
        }
    }


    /*
    * Navigation Block
    */


    // callback when likes count of post is clicked - opens likes screen
    override fun showLikesScreen(postId: String) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postId)
            .entityType(POST)
            .build()
        LikesActivity.start(requireContext(), likesScreenExtras)
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

    // Processes report action on entity
    private fun reportEntity(
        entityId: String,
        creatorId: String,
        @ReportType
        entityType: Int,
        postId: String,
        postViewType: Int? = null,
        parentCommentId: String? = null
    ) {
        //create extras for [ReportActivity]
        val reportExtras = ReportExtras.Builder()
            .entityId(entityId)
            .entityCreatorId(creatorId)
            .entityType(entityType)
            .postId(postId)
            .postViewType(postViewType)
            .parentCommentId(parentCommentId)
            .build()

        //get Intent for [ReportActivity]
        val intent = ReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
    }

    // launcher to start [ReportActivity] and show success dialog for result
    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringExtra(ReportFragment.REPORT_RESULT)
                ReportSuccessDialog(data ?: "").show(
                    childFragmentManager,
                    ReportSuccessDialog.TAG
                )
            }
        }


    /**
     * Adapter Util Block
     */


    // sets page-1 data of post and scrolls to top
    private fun setPostDataAndScrollToTop(post: PostViewData) {
        // [ArrayList] to add all the items to adapter
        val postDetailList = ArrayList<BaseViewType>()
        // adds the post data at [postDataPosition]
        postDetailList.add(postDataPosition, post)

        if (post.commentsCount == 0) {
            // adds no comments view data
            val noCommentViewData = NoCommentsViewData.Builder().build()
            postDetailList.add(noCommentViewData)
        } else {
            // adds commentsCountViewData if comments are present
            postDetailList.add(
                commentsCountPosition,
                ViewDataConverter.convertCommentsCount(post.commentsCount)
            )
        }

        val comments = post.replies.toList()
        // adds all the comments to the [postDetailList]
        postDetailList.addAll(comments)
        mPostDetailAdapter.replace(postDetailList)

        if (toFindComment) {
            //find the comments already present in adapter
            val index = mPostDetailAdapter.items().indexOfFirst {
                (it is CommentViewData) && (it.id == postDetailExtras.commentId)
            }

            //comment not present -> get it from api
            if (index == -1) {
                viewModel.getComment(post.id, postDetailExtras.commentId ?: "", 1)
            } else {
                //scroll to that comment
                binding.rvPostDetails.scrollToPosition(index)
            }
        } else {
            binding.rvPostDetails.scrollToPosition(postDataPosition)
        }
    }

    // updates the post and add comments to adapter
    private fun updatePostAndAddComments(post: PostViewData) {
        // notifies the subscribers about the change in post data
        postEvent.notify(Pair(post.id, post))

        // updates the post
        mPostDetailAdapter.update(postDataPosition, post)
        // adds the paginated comments
        mPostDetailAdapter.addAll(post.replies.toList())
    }

    // refreshes the whole post detail screen
    private fun refreshPostData() {
        mSwipeRefreshLayout.isRefreshing = true
        mScrollListener.resetData()
        fetchPostData()
    }

    override fun likePost(position: Int) {
        //get item
        val item = mPostDetailAdapter[position]
        if (item is PostViewData) {
            //new like count
            val newLikesCount = if (item.isLiked) {
                item.likesCount - 1
            } else {
                item.likesCount + 1
            }

            //update post view data
            val newViewData = item.toBuilder()
                .fromPostLiked(true)
                .isLiked(!item.isLiked)
                .likesCount(newLikesCount)
                .build()

            postEvent.notify(Pair(newViewData.id, newViewData))

            //call api
            postActionsViewModel.likePost(newViewData.id)
            //update recycler
            mPostDetailAdapter.update(position, newViewData)
        }
    }

    override fun savePost(position: Int) {
        //get item
        val item = mPostDetailAdapter[position]
        if (item is PostViewData) {
            //update the post view data
            val newViewData = item.toBuilder()
                .fromPostSaved(true)
                .isSaved(!item.isSaved)
                .build()

            // notifies the subscribers about the change
            postEvent.notify(Pair(newViewData.id, newViewData))

            //call api
            postActionsViewModel.savePost(newViewData.id)

            //update recycler
            mPostDetailAdapter.update(position, newViewData)
        }
    }

    // updates post view data when see more is clicked
    override fun updatePostSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        val item = mPostDetailAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .build()
            mPostDetailAdapter.update(position, newViewData)
        }
    }

    // updates comment view data when see more is clicked
    override fun updateCommentSeenFullContent(
        position: Int,
        alreadySeenFullContent: Boolean,
        parentCommentId: String?
    ) {
        val item =
            if (parentCommentId == null) {
                mPostDetailAdapter[position]
            } else {
                val indexAndComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
                val comment = indexAndComment.second
                comment.replies[position]
            }
        if (item is CommentViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .fromCommentLiked(false)
                .build()
            mPostDetailAdapter.update(position, newViewData)
        }
    }

    // adds the reply to its parentComment
    private fun addReplyToAdapter(parentCommentId: String, reply: CommentViewData) {
        // gets the parentComment from adapter
        val parentComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
        val parentIndex = parentComment.first
        val parentCommentViewData = parentComment.second

        // adds the reply at first
        parentCommentViewData.replies.add(0, reply)

        val newCommentViewData = parentCommentViewData.toBuilder()
            .repliesCount(parentCommentViewData.repliesCount + 1)
            .build()

        // updates the parentComment with added reply
        mPostDetailAdapter.update(parentIndex, newCommentViewData)

        // scroll to comment's position
        scrollToPositionWithOffset(parentIndex, 75)
    }

    // removes the reply from its parentComment
    private fun removeDeletedReply(parentCommentId: String, replyId: String) {
        // gets the parentComment from adapter
        val parentComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
        val parentIndex = parentComment.first
        val parentCommentViewData = parentComment.second

        // removes the reply with specified replyId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            parentCommentViewData.replies.removeIf {
                it.id == replyId
            }
        } else {
            val index = parentCommentViewData.replies.indexOfFirst {
                it.id == replyId
            }
            parentCommentViewData.replies.removeAt(index)
        }

        val newCommentViewData = parentCommentViewData.toBuilder()
            .repliesCount(parentCommentViewData.repliesCount - 1)
            .build()

        // updates the parentComment with removed reply
        mPostDetailAdapter.update(parentIndex, newCommentViewData)
    }

    // adds paginated replies to comment
    private fun addReplies(comment: CommentViewData, page: Int) {
        // gets comment from adapter
        val indexAndComment = getIndexAndCommentFromAdapter(comment.id)

        //if comment is not present in adapter
        if (indexAndComment == null) {
            //set to false because comment is added
            toFindComment = false
            //add comment to adapter
            mPostDetailAdapter.add(commentsStartPosition, comment)
            //scroll to the comment
            binding.rvPostDetails.scrollToPosition(commentsStartPosition)
        } else {
            val index = indexAndComment.first
            val adapterComment = indexAndComment.second
            if (page == 1) {
                // updates the comment with page-1 replies
                mPostDetailAdapter.update(index, comment)
                scrollToPositionWithOffset(index, 75)
            } else {
                // adds replies in adapter and fetched replies
                comment.replies.addAll(
                    0,
                    adapterComment.replies
                )
                mPostDetailAdapter.update(index, comment)
                scrollToPositionWithOffset(index + 1, 150)
            }
        }
    }

    // callback when comment/reply is liked
    override fun likeComment(commentId: String) {
        val indexAndComment = getIndexAndCommentFromAdapter(commentId) ?: return
        val position = indexAndComment.first
        val comment = indexAndComment.second
        //new like count
        val newLikesCount = if (comment.isLiked) {
            comment.likesCount - 1
        } else {
            comment.likesCount + 1
        }

        //update comment view data
        val newViewData = comment.toBuilder()
            .fromCommentLiked(true)
            .isLiked(!comment.isLiked)
            .likesCount(newLikesCount)
            .build()

        //call api
        viewModel.likeComment(newViewData.postId, newViewData.id)
        //update recycler
        mPostDetailAdapter.update(position, newViewData)
    }

    // callback when reply is liked by user
    override fun likeReply(parentCommentId: String, replyId: String) {
        // gets parentComment from adapter
        val parentIndexAndComment = getIndexAndCommentFromAdapter(parentCommentId) ?: return
        val position = parentIndexAndComment.first
        val parentComment = parentIndexAndComment.second

        // gets reply from the comment
        val reply = getIndexAndReplyFromComment(parentComment, replyId)
        val replyIndex = reply.first
        val replyViewData = reply.second

        //new like count
        val newLikesCount = if (replyViewData.isLiked) {
            replyViewData.likesCount - 1
        } else {
            replyViewData.likesCount + 1
        }

        val updatedReply = parentComment.replies[replyIndex]
            .toBuilder()
            .isLiked(!replyViewData.isLiked)
            .likesCount(newLikesCount)
            .build()

        parentComment.replies[replyIndex] = updatedReply

        //update comment view data
        val newViewData = parentComment.toBuilder()
            .replies(parentComment.replies)
            .build()

        //call api
        viewModel.likeComment(newViewData.postId, updatedReply.id)
        //update recycler
        mPostDetailAdapter.update(position, newViewData)
    }

    // callback when replying on a comment
    override fun replyOnComment(
        commentId: String,
        commentPosition: Int,
        parentCommenter: UserViewData
    ) {
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

    // callback when post menu items are clicked
    override fun onPostMenuItemClicked(
        postId: String,
        creatorId: String,
        title: String
    ) {
        when (title) {
            DELETE_POST_MENU_ITEM -> {
                deletePost(
                    postId,
                    creatorId
                )
            }
            REPORT_POST_MENU_ITEM -> {
                val postData = mPostDetailAdapter[postDataPosition] as PostViewData
                val postViewType = postData.viewType
                reportEntity(
                    postId,
                    creatorId,
                    REPORT_TYPE_POST,
                    postId,
                    postViewType = postViewType
                )
            }
            PIN_POST_MENU_ITEM -> {
                pinPost()
            }
            UNPIN_POST_MENU_ITEM -> {
                unpinPost()
            }
        }
    }

    private fun pinPost() {
        //get item
        val post = mPostDetailAdapter[postDataPosition] as PostViewData

        //get pin menu item
        val menuItems = post.menuItems.toMutableList()
        val pinPostIndex = menuItems.indexOfFirst {
            (it.title == PIN_POST_MENU_ITEM)
        }

        //if pin item doesn't exist
        if (pinPostIndex == -1) return

        //update pin menu item
        val pinPostMenuItem = menuItems[pinPostIndex]
        val newPinPostMenuItem = pinPostMenuItem.toBuilder().title(UNPIN_POST_MENU_ITEM).build()
        menuItems[pinPostIndex] = newPinPostMenuItem

        //update the post view data
        val newViewData = post.toBuilder()
            .isPinned(!post.isPinned)
            .menuItems(menuItems)
            .build()

        //call api
        postActionsViewModel.pinPost(post)

        //update recycler
        mPostDetailAdapter.update(postDataPosition, newViewData)

        // notifies the subscribers about the change in post data
        postEvent.notify(Pair(newViewData.id, newViewData))
    }

    private fun unpinPost() {
        //get item
        val post = mPostDetailAdapter[postDataPosition] as PostViewData

        //get unpin menu item
        val menuItems = post.menuItems.toMutableList()
        val unPinPostIndex = menuItems.indexOfFirst {
            (it.title == UNPIN_POST_MENU_ITEM)
        }

        //if unpin item doesn't exist
        if (unPinPostIndex == -1) return

        //update unpin menu item
        val unPinPostMenuItem = menuItems[unPinPostIndex]
        val newUnPinPostMenuItem =
            unPinPostMenuItem.toBuilder().title(PIN_POST_MENU_ITEM).build()
        menuItems[unPinPostIndex] = newUnPinPostMenuItem

        //update the post view data
        val newViewData = post.toBuilder()
            .isPinned(!post.isPinned)
            .menuItems(menuItems)
            .build()

        //call api
        postActionsViewModel.pinPost(post)

        //update recycler
        mPostDetailAdapter.update(postDataPosition, newViewData)

        // notifies the subscribers about the change in post data
        postEvent.notify(Pair(newViewData.id, newViewData))
    }

    // callback when replyCount is clicked to view replies
    override fun fetchReplies(commentId: String) {
        val comment = getIndexAndCommentFromAdapter(commentId)?.second ?: return

        // gets page-1 replies
        viewModel.getComment(
            comment.postId,
            comment.id,
            1
        )
    }

    // callback for comment's menu is item
    override fun onCommentMenuItemClicked(
        postId: String,
        commentId: String,
        creatorId: String,
        title: String
    ) {
        when (title) {
            DELETE_COMMENT_MENU_ITEM -> {
                deleteComment(
                    postId,
                    commentId,
                    creatorId
                )
            }
            REPORT_COMMENT_MENU_ITEM -> {
                reportEntity(
                    commentId,
                    creatorId,
                    REPORT_TYPE_COMMENT,
                    postId
                )
            }
        }
    }

    // callback when view more replies is clicked
    override fun viewMoreReplies(
        parentCommentId: String,
        page: Int
    ) {
        val comment = getIndexAndCommentFromAdapter(parentCommentId)?.second ?: return
        viewModel.getComment(
            comment.postId,
            parentCommentId,
            page
        )
    }

    // callback when the item of reply menu is clicked
    override fun onReplyMenuItemClicked(
        postId: String,
        parentCommentId: String,
        replyId: String,
        creatorId: String,
        title: String
    ) {
        when (title) {
            DELETE_COMMENT_MENU_ITEM -> {
                deleteComment(
                    postId,
                    replyId,
                    creatorId,
                    parentCommentId
                )
            }
            REPORT_COMMENT_MENU_ITEM -> {
                reportEntity(
                    replyId,
                    creatorId,
                    REPORT_TYPE_REPLY,
                    postId,
                    parentCommentId = parentCommentId
                )
            }
        }
    }

    // callback when self post is deleted by user
    override fun selfDelete(deleteExtras: DeleteExtras) {
        when (deleteExtras.entityType) {
            DELETE_TYPE_POST -> {
                val post = mPostDetailAdapter[postDataPosition] as PostViewData
                postActionsViewModel.deletePost(post)
            }
            DELETE_TYPE_COMMENT -> {
                val commentId = deleteExtras.commentId ?: return
                viewModel.deleteComment(
                    deleteExtras.postId,
                    commentId,
                    parentCommentId = deleteExtras.parentCommentId
                )
            }
        }
    }

    // callback when other's post is deleted by CM
    override fun adminDelete(deleteExtras: DeleteExtras, reason: String) {
        when (deleteExtras.entityType) {
            DELETE_TYPE_POST -> {
                val post = mPostDetailAdapter[postDataPosition] as PostViewData
                postActionsViewModel.deletePost(post, reason)
            }
            DELETE_TYPE_COMMENT -> {
                val commentId = deleteExtras.commentId ?: return
                viewModel.deleteComment(
                    deleteExtras.postId,
                    commentId,
                    parentCommentId = deleteExtras.parentCommentId,
                    reason = reason
                )
            }
        }
    }

    // callback when +x more text is clicked to see more documents
    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        if (position == mPostDetailAdapter.items().size - 1) {
            binding.rvPostDetails.post {
                scrollToPositionWithOffset(position, 75)
            }
        }

        mPostDetailAdapter.update(
            position,
            postData.toBuilder().isExpanded(true).build()
        )
    }

    // updates the fromPostLiked/fromPostSaved variables and updates the rv list
    override fun updateFromLikedSaved(position: Int) {
        var postData = mPostDetailAdapter[postDataPosition] as PostViewData
        postData = postData.toBuilder()
            .fromPostLiked(false)
            .fromPostSaved(false)
            .fromVideoAction(false)
            .build()
        mPostDetailAdapter.updateWithoutNotifyingRV(position, postData)
    }

    //get index and post from the adapter using postId
    private fun getIndexAndCommentFromAdapter(commentId: String): Pair<Int, CommentViewData>? {
        val index = mPostDetailAdapter.items().indexOfFirst {
            (it is CommentViewData) && (it.id == commentId)
        }

        if (index == -1) {
            return null
        }

        val comment = getCommentFromAdapter(index)

        return Pair(index, comment)
    }

    private fun getCommentFromAdapter(position: Int): CommentViewData {
        return mPostDetailAdapter.items()[position] as CommentViewData
    }

    //get index and reply from the parentComment using replyId
    private fun getIndexAndReplyFromComment(
        parentComment: CommentViewData,
        replyId: String
    ): Pair<Int, CommentViewData> {
        val index = parentComment.replies.indexOfFirst {
            it.id == replyId
        }

        val reply = parentComment.replies[index]

        return Pair(index, reply)
    }

    /**
     * Scroll to a position with offset from the top header
     * @param position Index of the item to scroll to
     * @param offset value with which to scroll
     */
    private fun scrollToPositionWithOffset(position: Int, offset: Int) {
        val px = (ViewUtils.dpToPx(offset) * 1.5).toInt()
        (binding.rvPostDetails.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
            position,
            px
        )
    }
}