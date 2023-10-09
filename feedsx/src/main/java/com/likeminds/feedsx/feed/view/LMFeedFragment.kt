package com.likeminds.feedsx.feed.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.likeminds.feedsx.*
import com.likeminds.feedsx.SDKApplication.Companion.LOG_TAG
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedFragmentFeedBinding
import com.likeminds.feedsx.delete.model.DELETE_TYPE_POST
import com.likeminds.feedsx.delete.model.DeleteExtras
import com.likeminds.feedsx.delete.view.LMFeedAdminDeleteDialogFragment
import com.likeminds.feedsx.delete.view.LMFeedSelfDeleteDialogFragment
import com.likeminds.feedsx.feed.adapter.LMFeedSelectedTopicAdapter
import com.likeminds.feedsx.feed.adapter.LMFeedSelectedTopicAdapterListener
import com.likeminds.feedsx.feed.model.LMFeedExtras
import com.likeminds.feedsx.feed.util.PostEvent
import com.likeminds.feedsx.feed.util.PostEvent.*
import com.likeminds.feedsx.feed.viewmodel.LMFeedViewModel
import com.likeminds.feedsx.likes.model.LikesScreenExtras
import com.likeminds.feedsx.likes.model.POST
import com.likeminds.feedsx.likes.view.LMFeedLikesActivity
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.util.PostVideoAutoPlayHelper
import com.likeminds.feedsx.media.view.LMFeedMediaPickerActivity
import com.likeminds.feedsx.notificationfeed.view.LMFeedNotificationFeedActivity
import com.likeminds.feedsx.overflowmenu.model.*
import com.likeminds.feedsx.post.create.model.LMFeedCreatePostExtras
import com.likeminds.feedsx.post.create.view.*
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity
import com.likeminds.feedsx.post.edit.viewmodel.LMFeedHelperViewModel
import com.likeminds.feedsx.post.viewmodel.PostActionsViewModel
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.model.VIDEO
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.report.model.REPORT_TYPE_POST
import com.likeminds.feedsx.report.model.ReportExtras
import com.likeminds.feedsx.report.view.*
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionExtras
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionActivity
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionActivity.Companion.TOPIC_SELECTION_RESULT_EXTRAS
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.model.BaseViewType
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject

class LMFeedFragment :
    BaseFragment<LmFeedFragmentFeedBinding, LMFeedViewModel>(),
    PostAdapterListener,
    LMFeedAdminDeleteDialogFragment.DeleteDialogListener,
    LMFeedSelfDeleteDialogFragment.DeleteAlertDialogListener,
    PostObserver,
    LMFeedCreateResourceDialog.CreateResourceDialogListener,
    LMFeedLinkResourceDialogFragment.LinkResourceDialogListener,
    LMFeedSelectedTopicAdapterListener {

    companion object {

        const val TAG = "LMFeedFragment"
        private const val LM_FEED_EXTRAS = "LM_FEED_EXTRAS"
        const val VIDEO_DURATION_LIMIT = 600
        const val PDF_SIZE_LIMIT = 8 * 1024 * 1024

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val POST_NOTIFICATIONS = Manifest.permission.POST_NOTIFICATIONS

        /**
         * creates a instance of fragment
         **/
        @JvmStatic
        fun getInstance(
            extras: LMFeedExtras,
        ): LMFeedFragment {
            val fragment = LMFeedFragment()
            val bundle = Bundle()
            bundle.putParcelable(LM_FEED_EXTRAS, extras)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var lmFeedExtras: LMFeedExtras

    // shared viewModel between [FeedFragment] and [PostDetailFragment] for postActions
    @Inject
    lateinit var postActionsViewModel: PostActionsViewModel

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    @Inject
    lateinit var lmFeedHelperViewModel: LMFeedHelperViewModel

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mPostAdapter: PostAdapter
    private lateinit var mScrollListener: EndlessRecyclerScrollListener
    private lateinit var postVideoAutoPlayHelper: PostVideoAutoPlayHelper
    private lateinit var mSelectedTopicAdapter: LMFeedSelectedTopicAdapter

    private var createPostDialog: LMFeedCreateResourceDialog? = null

    // variable to check if there is a post already uploading
    private var alreadyPosting: Boolean = false
    private val workersMap by lazy { ArrayList<UUID>() }

    private val postPublisher by lazy {
        PostEvent.getPublisher()
    }

    override val useSharedViewModel: Boolean
        get() = true

    override fun getViewModelClass(): Class<LMFeedViewModel> {
        return LMFeedViewModel::class.java
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().feedComponent()?.inject(this)
    }

    override fun getViewBinding(): LmFeedFragmentFeedBinding {
        return LmFeedFragmentFeedBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(LM_FEED_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        lmFeedExtras = ExtrasUtil.getParcelable(
            arguments,
            LM_FEED_EXTRAS,
            LMFeedExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun setUpViews() {
        super.setUpViews()
        checkNotificationPermission()
        initUI()
        initiateSDK()
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
                if (activity?.checkSelfPermission(POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    notificationPermissionLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun observeData() {
        super.observeData()
        observePosting()

        // observes userResponse LiveData
        initiateViewModel.userResponse.observe(viewLifecycleOwner) {
            observeUserResponse()
        }

        // observes hasCreatePostRights LiveData
        initiateViewModel.hasCreatePostRights.observe(viewLifecycleOwner) {
            initNewPostClick(it)
        }

        // observes logoutResponse LiveData
        initiateViewModel.logoutResponse.observe(viewLifecycleOwner) {
            Log.d(
                LOG_TAG,
                "initiate api sdk called -> success and have not app access"
            )
            showInvalidAccess()
        }

        initiateViewModel.initiateErrorMessage.observe(viewLifecycleOwner) {
            removeShimmer()
            ViewUtils.showErrorMessageToast(requireContext(), it)
        }

        // observe unread notification count
        viewModel.unreadNotificationCount.observe(viewLifecycleOwner) { count ->
            SDKApplication.getLMFeedUICallback()?.updateNotificationCount(count)
        }

        // observe universal feed
        viewModel.universalFeedResponse.observe(viewLifecycleOwner) { pair ->
            observeFeedUniversal(pair)
        }

        lmFeedHelperViewModel.showTopicFilter.observe(viewLifecycleOwner) { showTopicFilter ->
            binding.layoutAllTopics.root.isVisible = showTopicFilter
        }

        // observes deletePostResponse LiveData
        postActionsViewModel.deletePostResponse.observe(viewLifecycleOwner) { postId ->
            val indexToRemove = getIndexAndPostFromAdapter(postId)?.first ?: return@observe
            mPostAdapter.removeIndex(indexToRemove)
            checkForNoPost(mPostAdapter.items())
            refreshAutoPlayer()
            ViewUtils.showShortToast(
                requireContext(),
                getString(R.string.post_deleted)
            )
        }

        // observes pinPostResponse LiveData
        postActionsViewModel.pinPostResponse.observe(viewLifecycleOwner) { postId ->
            val post = getIndexAndPostFromAdapter(postId)?.second ?: return@observe
            if (post.isPinned) {
                ViewUtils.showShortToast(requireContext(), getString(R.string.post_pinned_to_top))
            } else {
                ViewUtils.showShortToast(requireContext(), getString(R.string.post_unpinned))
            }
        }

        //observes errorMessage for the apis
        viewModel.errorMessageEventFlow.onEach { response ->
            observeErrorMessage(response)
        }.observeInLifecycle(viewLifecycleOwner)

        //observes errorMessage for the apis in shared view model
        postActionsViewModel.errorMessageEventFlow.onEach { response ->
            observeSharedErrorMessage(response)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // observes user response from InitiateUser
    private fun observeUserResponse() {
        viewModel.getUnreadNotificationCount()
        lmFeedHelperViewModel.getAllTopics(false)
        viewModel.getUniversalFeed(
            1,
            viewModel.getTopicIdsFromAdapterList(mSelectedTopicAdapter.items())
        )
    }

    //observe feed response
    private fun observeFeedUniversal(pair: Pair<Int, List<PostViewData>>) {
        removeShimmer()

        //page in api send
        val page = pair.first

        //list of post
        val feed = pair.second

        //if pull to refresh is called
        if (mSwipeRefreshLayout.isRefreshing) {
            checkForNoPost(feed)
            setFeedAndScrollToTop(feed)
            mSwipeRefreshLayout.isRefreshing = false
            return
        }

        binding.recyclerView.addOnScrollListener(mScrollListener)

        //normal adding
        if (page == 1) {
            checkForNoPost(feed)
            setFeedAndScrollToTop(feed)
        } else {
            mPostAdapter.addAll(feed)
            refreshAutoPlayer()
        }
    }

    // observes post live data
    private fun observePosting() {
        viewModel.postDataEventFlow.onEach { response ->
            when (response) {
                // when the post data comes from local db
                is LMFeedViewModel.PostDataEvent.PostDbData -> {
                    alreadyPosting = true
                    val post = response.post
                    if (!isFirstItemShimmer()) {
                        addShimmer()
                    }
                    observeMediaUpload(post)
                }
                // when the post data comes from api response
                is LMFeedViewModel.PostDataEvent.PostResponseData -> {
                    binding.apply {
                        ViewUtils.showShortToast(requireContext(), getString(R.string.post_created))
                        removePostingView()
                        scrollToPositionWithOffset(0)
                        mPostAdapter.add(0, response.post)
                        refreshAutoPlayer()
                    }
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // checks if there is any post or not
    private fun checkForNoPost(feed: List<BaseViewType>) {
        binding.apply {
            if (feed.isNotEmpty()) {
                layoutNoPost.root.hide()
                newPostButton.show()
                recyclerView.show()
            } else {
                layoutNoPost.root.show()
                newPostButton.hide()
                recyclerView.hide()
            }
        }
    }

    // starts notification feed activity
    fun startNotificationFeed() {
        viewModel.sendNotificationPageOpenedEvent()
        LMFeedNotificationFeedActivity.start(requireContext())
    }

    // finds the upload worker by UUID and observes the worker
    private fun observeMediaUpload(postingData: PostViewData) {
        if (postingData.workerUUID.isEmpty()) {
            return
        }
        val uuid = UUID.fromString(postingData.workerUUID)
        if (!workersMap.contains(uuid)) {
            workersMap.add(uuid)
            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(uuid)
                .observe(viewLifecycleOwner) { workInfo ->
                    observeMediaWorker(workInfo, postingData)
                }
        }
    }

    // observes the media worker through various worker lifecycle
    private fun observeMediaWorker(
        workInfo: WorkInfo,
        postingData: PostViewData
    ) {
        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
                // uploading completed, call the add post api
                viewModel.addPost(postingData)
            }

            WorkInfo.State.FAILED -> {
                ViewUtils.showShortToast(requireContext(), getString(R.string.something_went_wrong))
                viewModel.deletePostFromDB(postingData.temporaryId ?: 0L)
                removePostingView()
            }

            else -> {}
        }
    }

    //observe error handling
    private fun observeErrorMessage(response: LMFeedViewModel.ErrorMessageEvent) {
        when (response) {
            is LMFeedViewModel.ErrorMessageEvent.UniversalFeed -> {
                val errorMessage = response.errorMessage
                mSwipeRefreshLayout.isRefreshing = false
                removeShimmer()
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }

            is LMFeedViewModel.ErrorMessageEvent.AddPost -> {
                ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                removePostingView()
            }

            is LMFeedViewModel.ErrorMessageEvent.GetUnreadNotificationCount -> {
                ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
            }
        }
    }

    private fun observeSharedErrorMessage(response: PostActionsViewModel.ErrorMessageEvent) {
        when (response) {
            is PostActionsViewModel.ErrorMessageEvent.LikePost -> {
                val postId = response.postId

                //get post and index
                val pair = getIndexAndPostFromAdapter(postId) ?: return
                val post = pair.second
                val index = pair.first

                //update post view data
                val updatedPost = post.toBuilder()
                    .isLiked(false)
                    .fromPostLiked(true)
                    .likesCount(post.likesCount - 1)
                    .build()

                //update recycler view
                mPostAdapter.update(index, updatedPost)

                //show error message
                val errorMessage = response.errorMessage
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }

            is PostActionsViewModel.ErrorMessageEvent.SavePost -> {
                val postId = response.postId

                //get post and index
                val pair = getIndexAndPostFromAdapter(postId) ?: return
                val post = pair.second
                val index = pair.first

                //update post view data
                val updatedPost = post.toBuilder()
                    .isSaved(false)
                    .fromPostSaved(true)
                    .build()

                //update recycler view
                mPostAdapter.update(index, updatedPost)

                //show error message
                val errorMessage = response.errorMessage
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }

            is PostActionsViewModel.ErrorMessageEvent.DeletePost -> {
                val errorMessage = response.errorMessage
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }

            is PostActionsViewModel.ErrorMessageEvent.PinPost -> {
                val postId = response.postId

                //get post and index
                val pair = getIndexAndPostFromAdapter(postId) ?: return
                val post = pair.second
                val index = pair.first

                //update post view data
                val updatedPost = post.toBuilder()
                    .isPinned(!post.isPinned)
                    .build()

                //update recycler view
                mPostAdapter.update(index, updatedPost)

                //show error message
                val errorMessage = response.errorMessage
                ViewUtils.showErrorMessageToast(requireContext(), errorMessage)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        postPublisher.subscribe(this)
    }

    override fun onResume() {
        super.onResume()

        // sends feed opened event
        viewModel.sendFeedOpenedEvent()

        val temporaryId = viewModel.getTemporaryId()
        if (temporaryId != -1L && !alreadyPosting) {
            removePostingView()
            viewModel.fetchPendingPostFromDB()
        }
        if (this.isVisible) {
            initiateAutoPlayer("onResume")
        }
    }

    override fun onPause() {
        super.onPause()
        destroyAutoPlayer("onPause")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (hidden) {
            Log.d("PUI", "onHiddenChanged111: ")
            destroyAutoPlayer("hidden")
        } else {
            Log.d("PUI", "onHiddenChanged: ")
            // sends feed opened event
            viewModel.sendFeedOpenedEvent()

            val temporaryId = viewModel.getTemporaryId()
            if (temporaryId != -1L && !alreadyPosting) {
                removePostingView()
                viewModel.fetchPendingPostFromDB()
            }
            initiateAutoPlayer("not hidden")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // unsubscribes itself from the [PostPublisher]
        postPublisher.unsubscribe(this)
    }

    // initiates SDK
    private fun initiateSDK() {
        addShimmer()
        initiateViewModel.initiateUser(
            requireContext(),
            lmFeedExtras.apiKey,
            lmFeedExtras.userName,
            lmFeedExtras.uuid,
            lmFeedExtras.isGuest
        )
    }

    // add shimmer items
    private fun addShimmer() {
        scrollToPositionWithOffset(0)
        mPostAdapter.add(0, PostShimmerViewData.Builder().build())
    }

    // remove shimmer
    private fun removeShimmer() {
        if (isFirstItemShimmer()) {
            mPostAdapter.removeIndex(0)
        }
    }

    // checks whether first item in adapter is shimmer or not
    private fun isFirstItemShimmer(): Boolean {
        if (mPostAdapter.itemCount > 0 && mPostAdapter.items().first() is PostShimmerViewData) {
            return true
        }
        return false
    }

    /**
     * UI Block
     **/

    // initializes various UI components
    private fun initUI() {
        binding.toolbarColor = LMFeedBranding.getToolbarColor()

        setStatusBarColor()
        initFeedRecyclerView()
        initSelectedTopicRecyclerView()
        initNewPostClick(true)
        initTopicFilterClick()
        initSwipeRefreshLayout()
    }

    @Suppress("Deprecation")
    private fun setStatusBarColor() {
        requireActivity().window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = LMFeedBranding.getHeaderColor()
            @RequiresApi(Build.VERSION_CODES.M)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    // initializes new post fab
    private fun initNewPostClick(hasCreatePostRights: Boolean) {
        binding.apply {
            // sets color of fab button as per user rights
            if (hasCreatePostRights) {
                layoutNoPost.fabNewPost.backgroundTintList =
                    ColorStateList.valueOf(LMFeedBranding.getButtonsColor())
                newPostButton.backgroundTintList =
                    ColorStateList.valueOf(LMFeedBranding.getButtonsColor())
            } else {
                layoutNoPost.fabNewPost.backgroundTintList =
                    ColorStateList.valueOf(Color.GRAY)
                newPostButton.backgroundTintList =
                    ColorStateList.valueOf(Color.GRAY)
            }

            layoutNoPost.fabNewPost.setOnClickListener {
                handleNewPostClick(hasCreatePostRights)
            }

            newPostButton.setOnClickListener {
                handleNewPostClick(hasCreatePostRights)
            }
        }
    }

    // handles new post fab click
    private fun handleNewPostClick(hasCreatePostRights: Boolean) {
        binding.apply {
            if (hasCreatePostRights) {
                if (alreadyPosting) {
                    ViewUtils.showShortToast(
                        requireContext(),
                        getString(R.string.a_post_is_already_uploading)
                    )
                } else {
                    createPostDialog = LMFeedCreateResourceDialog.show(childFragmentManager)
                }
            } else {
                ViewUtils.showShortSnack(
                    root,
                    getString(R.string.you_do_not_have_permission_to_create_a_post)
                )
            }
        }
    }

    // launcher for [CreatePostActivity]
    private val createPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    // post of type text/link has been created and posted
                    refreshFeed()
                }

                LMFeedCreatePostActivity.RESULT_UPLOAD_POST -> {
                    // post with attachments created, now upload and post it from db
                    viewModel.fetchPendingPostFromDB()
                }
            }
        }

    // launcher to handle gallery (IMAGE/VIDEO) intent
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = ExtrasUtil.getParcelable(
                    result.data?.extras,
                    LMFeedMediaPickerActivity.ARG_MEDIA_PICKER_RESULT,
                    MediaPickerResult::class.java
                )
                checkMediaPickedResult(data)
            }
        }

    private val mediaBrowseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onMediaPickedFromGallery(result.data)
            }
        }

    private val documentBrowseLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                onPdfPicked(result.data)
            }
        }

    // process the result obtained from media picker
    private fun checkMediaPickedResult(result: MediaPickerResult?) {
        if (result != null) {
            when (result.mediaPickerResultType) {
                MEDIA_RESULT_BROWSE -> {
                    if (MediaType.isPDF(result.mediaTypes)) {
                        val intent = AndroidUtils.getExternalDocumentPickerIntent(
                            allowMultipleSelect = result.allowMultipleSelect
                        )
                        documentBrowseLauncher.launch(intent)
                    } else {
                        val intent = AndroidUtils.getExternalPickerIntent(
                            result.mediaTypes,
                            result.allowMultipleSelect,
                            result.browseClassName
                        )
                        if (intent != null)
                            mediaBrowseLauncher.launch(intent)
                    }
                }

                MEDIA_RESULT_PICKED -> {
                    onMediaPicked(result)
                }
            }
        }
    }

    // converts the picked media to SingleUriData and adds to the selected media
    private fun onMediaPicked(result: MediaPickerResult) {
        val data =
            MediaUtils.convertMediaViewDataToSingleUriData(requireContext(), result.medias)
        // sends media attached event with media type and count
        viewModel.sendMediaAttachedEvent(data)
        if (data.isNotEmpty()) {
            val attachmentType = getAttachmentType(data.first().fileType)
            if (checkForValidAttachment(data.first())) {
                val createPostExtras = LMFeedCreatePostExtras.Builder()
                    .attachmentType(attachmentType)
                    .attachmentUri(data.first())
                    .source(LMFeedAnalytics.Source.UNIVERSAL_FEED)
                    .isAdmin(initiateViewModel.isAdmin.value ?: false)
                    .build()
                startCreatePostActivity(createPostExtras)
            }
        }
    }

    // checks if the selected attachment is allowed or not
    private fun checkForValidAttachment(uri: SingleUriData?): Boolean {
        return when (uri?.fileType) {
            com.likeminds.feedsx.media.model.VIDEO -> {
                if (uri.duration != null && uri.duration > VIDEO_DURATION_LIMIT) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.video_duration_must_be_less_than_10_min),
                        Toast.LENGTH_SHORT
                    ).show()
                    false
                } else {
                    true
                }
            }

            else -> {
                true
            }
        }
    }

    private fun onMediaPickedFromGallery(data: Intent?) {
        val uris = MediaUtils.getExternalIntentPickerUris(data)
        viewModel.fetchUriDetails(requireContext(), uris) {
            val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                requireContext(), it
            )
            // sends media attached event with media type and count
            viewModel.sendMediaAttachedEvent(mediaUris)
            if (mediaUris.isNotEmpty()) {
                val attachmentType = getAttachmentType(mediaUris.first().fileType)
                if (checkForValidAttachment(mediaUris.first())) {
                    val createPostExtras = LMFeedCreatePostExtras.Builder()
                        .attachmentType(attachmentType)
                        .attachmentUri(mediaUris.first())
                        .source(LMFeedAnalytics.Source.UNIVERSAL_FEED)
                        .isAdmin(initiateViewModel.isAdmin.value ?: false)
                        .build()
                    startCreatePostActivity(createPostExtras)
                }
            }
        }
    }

    private fun onPdfPicked(data: Intent?) {
        val uris = MediaUtils.getExternalIntentPickerUris(data)
        viewModel.fetchUriDetails(requireContext(), uris) {
            val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                requireContext(), it
            )
            // sends media attached event with media type and count
            viewModel.sendMediaAttachedEvent(mediaUris)
            if (mediaUris.isNotEmpty()) {
                val attachmentType = getAttachmentType(mediaUris.first().fileType)
                if (checkForValidAttachment(mediaUris.first())) {
                    val createPostExtras = LMFeedCreatePostExtras.Builder()
                        .attachmentType(attachmentType)
                        .attachmentUri(mediaUris.first())
                        .source(LMFeedAnalytics.Source.UNIVERSAL_FEED)
                        .isAdmin(initiateViewModel.isAdmin.value ?: false)
                        .build()
                    startCreatePostActivity(createPostExtras)
                }
            }
        }
    }

    private fun getAttachmentType(fileType: String): Int {
        return when (fileType) {
            com.likeminds.feedsx.media.model.IMAGE -> {
                ARTICLE
            }

            com.likeminds.feedsx.media.model.VIDEO -> {
                VIDEO
            }

            PDF -> {
                DOCUMENT
            }

            else -> {
                ARTICLE
            }
        }
    }

    // initializes universal feed recyclerview
    private fun initFeedRecyclerView() {
        // item decorator to add spacing between items
        val dividerItemDecorator =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        dividerItemDecorator.setDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.feed_item_divider
            ) ?: return
        )
        val linearLayoutManager = LinearLayoutManager(context)
        mPostAdapter = PostAdapter(this)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = mPostAdapter
            if (itemAnimator is SimpleItemAnimator)
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            addItemDecoration(dividerItemDecorator)
            show()

            if (layoutManager is LinearLayoutManager) {
                mScrollListener =
                    object : EndlessRecyclerScrollListener((layoutManager as LinearLayoutManager)) {
                        override fun onLoadMore(currentPage: Int) {
                            if (currentPage > 0) {
                                viewModel.getUniversalFeed(
                                    currentPage,
                                    viewModel.getTopicIdsFromAdapterList(mSelectedTopicAdapter.items())
                                )
                            }
                        }

                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            binding.apply {
                                val isExtended = newPostButton.isExtended

                                // Scroll down
                                if (dy > 20 && isExtended) {
                                    newPostButton.shrink()
                                }

                                // Scroll up
                                if (dy < -20 && !isExtended) {
                                    newPostButton.extend()
                                }

                                // At the top
                                if (!recyclerView.canScrollVertically(-1)) {
                                    newPostButton.extend()
                                }
                            }
                        }
                    }
            }
        }
    }

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            LMFeedBranding.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            refreshFeed()
        }
    }

    //init topic filter view which shows all topics
    private fun initTopicFilterClick() {
        binding.layoutAllTopics.root.setOnClickListener {
            //show topics selecting screen with All topic filter
            val intent = LMFeedTopicSelectionActivity.getIntent(
                requireContext(),
                LMFeedTopicSelectionExtras.Builder()
                    .showAllTopicFilter(true)
                    .showEnabledTopicOnly(false)
                    .build()
            )

            topicSelectionLauncher.launch(intent)
        }
    }


    private val topicSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundle = result.data?.extras
                val resultExtras = ExtrasUtil.getParcelable(
                    bundle,
                    TOPIC_SELECTION_RESULT_EXTRAS,
                    LMFeedTopicSelectionResultExtras::class.java
                ) ?: return@registerForActivityResult

                handleTopicSelectionResult(resultExtras)
            }
        }

    //handles result after selecting filters and show recyclers views
    private fun handleTopicSelectionResult(resultExtras: LMFeedTopicSelectionResultExtras) {
        binding.apply {
            mScrollListener.resetData()
            mPostAdapter.clearAndNotify()

            if (resultExtras.isAllTopicSelected) {
                //show layouts accordingly
                layoutAllTopics.root.show()
                layoutSelectedTopics.root.hide()

                //call api
                mPostAdapter.clearAndNotify()
                addShimmer()
                viewModel.getUniversalFeed(1, null)
            } else {
                //show layouts accordingly
                layoutAllTopics.root.hide()
                layoutSelectedTopics.root.show()

                //set selected topics to filter
                val selectedTopics = resultExtras.selectedTopics
                mSelectedTopicAdapter.replace(selectedTopics)

                //call api
                mPostAdapter.clearAndNotify()
                addShimmer()
                viewModel.getUniversalFeed(
                    1,
                    viewModel.getTopicIdsFromAdapterList(mSelectedTopicAdapter.items())
                )
            }
        }
    }

    //init selected topic recycler view
    private fun initSelectedTopicRecyclerView() {
        mSelectedTopicAdapter = LMFeedSelectedTopicAdapter(this)
        binding.layoutSelectedTopics.apply {
            //set adapter
            rvSelectedTopics.adapter = mSelectedTopicAdapter
            rvSelectedTopics.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

            //set clear listener
            tvClear.setOnClickListener {
                clearTopics()
            }
        }
    }

    override fun topicCleared(position: Int) {
        if (mSelectedTopicAdapter.itemCount == 1) {
            clearTopics()
        } else {
            //remove from adapter
            mSelectedTopicAdapter.removeIndexWithNotifyDataSetChanged(position)

            //call apis
            mScrollListener.resetData()
            mPostAdapter.clearAndNotify()
            addShimmer()
            viewModel.getUniversalFeed(
                1,
                viewModel.getTopicIdsFromAdapterList(mSelectedTopicAdapter.items())
            )
        }
    }

    //clear all selected topics and reset data
    private fun clearTopics() {
        //call api
        mSelectedTopicAdapter.clearAndNotify()
        mScrollListener.resetData()
        mPostAdapter.clearAndNotify()
        addShimmer()
        viewModel.getUniversalFeed(1, null)

        //show layout accordingly
        binding.layoutSelectedTopics.root.hide()
        binding.layoutAllTopics.root.show()
    }

    //set posts through diff utils and scroll to top of the feed
    private fun setFeedAndScrollToTop(feed: List<PostViewData>) {
        mPostAdapter.replace(feed)
        binding.recyclerView.scrollToPosition(0)
        refreshAutoPlayer()
    }

    //refresh the whole feed
    private fun refreshFeed() {
        mSwipeRefreshLayout.isRefreshing = true
        mScrollListener.resetData()
        viewModel.getUnreadNotificationCount()
        viewModel.getUniversalFeed(
            1,
            viewModel.getTopicIdsFromAdapterList(mSelectedTopicAdapter.items())
        )
    }

    // shows invalid access error and logs out invalid user
    private fun showInvalidAccess() {
        binding.apply {
            removeShimmer()
            recyclerView.hide()
            layoutAccessRemoved.root.show()
        }
    }

    // removes the posting view and shows create post button
    private fun removePostingView() {
        binding.apply {
            removeShimmer()
            alreadyPosting = false
        }
    }

    /**
     * Post Actions block
     **/

// updates [alreadySeenFullContent] for the post
    override fun updatePostSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        // get item from adapter
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            // update the post view data
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .fromPostSaved(false)
                .fromPostLiked(false)
                .build()
            mPostAdapter.update(position, newViewData)
        }
    }

    // calls the savePost api and updates the post in adapter
    override fun savePost(position: Int) {
        //get item
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            //update the post view data
            val newViewData = item.toBuilder()
                .fromPostSaved(true)
                .isSaved(!item.isSaved)
                .build()

            //call api
            postActionsViewModel.savePost(newViewData.id)

            //update recycler
            mPostAdapter.update(position, newViewData)
        }
    }

    // calls the likePost api and updates the post in adapter
    override fun likePost(position: Int) {
        //get item
        val item = mPostAdapter[position]
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

            //call api
            postActionsViewModel.likePost(newViewData.id)
            //update recycler
            mPostAdapter.update(position, newViewData)
        }
    }

    // opens likes screen when likes count is clicked.
    override fun showLikesScreen(postId: String) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postId)
            .entityType(POST)
            .build()
        LMFeedLikesActivity.start(requireContext(), likesScreenExtras)
    }

    //opens post detail screen when add comment/comments count is clicked
    override fun comment(postId: String) {
        // sends comment list open event
        viewModel.sendCommentListOpenEvent()

        val postDetailExtras = PostDetailExtras.Builder()
            .postId(postId)
            .isEditTextFocused(true)
            .build()
        PostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //opens post detail screen when post content is clicked
    override fun postDetail(postId: String) {
        // sends comment list open event
        viewModel.sendCommentListOpenEvent()

        val postDetailExtras = PostDetailExtras.Builder()
            .postId(postId)
            .isEditTextFocused(false)
            .build()
        PostDetailActivity.start(requireContext(), postDetailExtras)
    }

    // callback when self post is deleted by user
    override fun selfDelete(deleteExtras: DeleteExtras) {
        val post = getIndexAndPostFromAdapter(deleteExtras.postId)?.second ?: return
        postActionsViewModel.deletePost(post)
    }

    // callback when other's post is deleted by CM
    override fun adminDelete(deleteExtras: DeleteExtras, reason: String) {
        val post = getIndexAndPostFromAdapter(deleteExtras.postId)?.second ?: return
        postActionsViewModel.deletePost(post, reason)
    }

    // updates the fromPostLiked/fromPostSaved variables and updates the rv list
    override fun updateFromLikedSaved(position: Int) {
        var postData = mPostAdapter[position] as PostViewData
        postData = postData.toBuilder()
            .fromPostLiked(false)
            .fromPostSaved(false)
            .build()
        mPostAdapter.updateWithoutNotifyingRV(position, postData)
    }

    // callback when user clicks to share the post
    override fun sharePost(postId: String) {
        ShareUtils.sharePost(
            requireContext(),
            postId,
            ShareUtils.domain
        )
        val post = getIndexAndPostFromAdapter(postId)?.second ?: return
        postActionsViewModel.sendPostShared(post)
    }

    // processes delete post request
    private fun deletePost(postId: String, postCreatorUUID: String) {
        val deleteExtras = DeleteExtras.Builder()
            .postId(postId)
            .entityType(DELETE_TYPE_POST)
            .build()

        if (postCreatorUUID == postActionsViewModel.getUUID()) {
            // if the post was created by current user
            LMFeedSelfDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        } else {
            // if the post was not created by current user and they are admin
            LMFeedAdminDeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        }
    }

    // Processes report action on post
    private fun reportPost(postId: String, uuid: String) {
        val post = getIndexAndPostFromAdapter(postId)?.second ?: return
        //create extras for [ReportActivity]
        val reportExtras = ReportExtras.Builder()
            .entityId(postId)
            .uuid(uuid)
            .entityType(REPORT_TYPE_POST)
            .postViewType(post.viewType)
            .build()

        //get Intent for [ReportActivity]
        val intent = LMFeedReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
    }

    // launcher to start [ReportActivity] and show success dialog for result
    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringExtra(LMFeedReportFragment.REPORT_RESULT)
                LMFeedReportSuccessDialog(data ?: "").show(
                    childFragmentManager,
                    LMFeedReportSuccessDialog.TAG
                )
            }
        }

    // calls the pinPost api and updates pins the post in adapter
    private fun pinPost(postId: String) {
        //get item
        val postAndIndex = getIndexAndPostFromAdapter(postId) ?: return
        val index = postAndIndex.first
        val post = postAndIndex.second

        //get pin menu item
        val menuItems = post.menuItems.toMutableList()
        val pinPostIndex = menuItems.indexOfFirst {
            (it.id == PIN_POST_MENU_ITEM_ID)
        }

        //if pin item doesn't exist
        if (pinPostIndex == -1) return

        //update pin menu item
        val pinPostMenuItem = menuItems[pinPostIndex]
        val newPinPostMenuItem =
            pinPostMenuItem.toBuilder().id(UNPIN_POST_MENU_ITEM_ID)
                .title(getString(R.string.unpin_this_post))
                .build()
        menuItems[pinPostIndex] = newPinPostMenuItem

        //update the post view data
        val newViewData = post.toBuilder()
            .isPinned(!post.isPinned)
            .menuItems(menuItems)
            .build()

        //call api
        postActionsViewModel.pinPost(post)

        //update recycler
        mPostAdapter.update(index, newViewData)
    }

    // calls the pinPost api and updates unpins the post in adapter
    private fun unpinPost(postId: String) {
        //get item
        val postAndIndex = getIndexAndPostFromAdapter(postId) ?: return
        val index = postAndIndex.first
        val post = postAndIndex.second

        //get unpin menu item
        val menuItems = post.menuItems.toMutableList()
        val unPinPostIndex = menuItems.indexOfFirst {
            (it.id == UNPIN_POST_MENU_ITEM_ID)
        }

        //if unpin item doesn't exist
        if (unPinPostIndex == -1) return

        //update unpin menu item
        val unPinPostMenuItem = menuItems[unPinPostIndex]
        val newUnPinPostMenuItem =
            unPinPostMenuItem.toBuilder().id(PIN_POST_MENU_ITEM_ID)
                .title(getString(R.string.pin_this_post))
                .build()
        menuItems[unPinPostIndex] = newUnPinPostMenuItem

        //update the post view data
        val newViewData = post.toBuilder()
            .isPinned(!post.isPinned)
            .menuItems(menuItems)
            .build()

        //call api
        postActionsViewModel.pinPost(post)

        //update recycler
        mPostAdapter.update(index, newViewData)
    }

    /**
     * Media Block
     **/

    /**
     * Initializes the [postVideoAutoPlayHelper] with the recyclerView
     * And starts observing
     **/
    private fun initiateAutoPlayer(source: String = "nothing") {
        Log.d("PUI", "initiateAutoPlayer: $source ${binding.recyclerView.isVisible}")
        postVideoAutoPlayHelper = PostVideoAutoPlayHelper.getInstance(binding.recyclerView)
        postVideoAutoPlayHelper.attachScrollListenerForVideo()
        postVideoAutoPlayHelper.playMostVisibleItem(source)
    }

    // removes the old player and refreshes auto play
    private fun refreshAutoPlayer() {
        if (!::postVideoAutoPlayHelper.isInitialized) {
            initiateAutoPlayer("refreshAutoPlayer")
        }
        postVideoAutoPlayHelper.removePlayer("refreshAutoPlayer")
        postVideoAutoPlayHelper.playMostVisibleItem("refresh")
    }

    // shows all attachment documents in list view and updates [isExpanded]
    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        if (position == mPostAdapter.items().size - 1) {
            scrollToPositionWithOffset(position)
        }

        mPostAdapter.update(
            position,
            postData.toBuilder()
                .isExpanded(true)
                .fromPostSaved(false)
                .fromPostLiked(false)
                .build()
        )
    }

    // callback when publisher publishes any updated postData
    override fun update(postData: Pair<String, PostViewData?>) {
        val postId = postData.first
        // fetches post from adapter
        val postIndex = getIndexAndPostFromAdapter(postId)?.first ?: return

        val updatedPost = postData.second

        // updates the item in adapter
        if (updatedPost == null) {
            // Post was deleted!
            mPostAdapter.removeIndex(postIndex)
        } else {
            // Post was updated
            mPostAdapter.update(postIndex, updatedPost)
        }
    }

    override fun onResourceSelected(attachmentType: Int) {
        createPostDialog?.dismiss()
        startPostCreation(attachmentType)
    }

    override fun linkOgTags(linkOGTags: LinkOGTagsViewData) {
        val createPostExtras = LMFeedCreatePostExtras.Builder()
            .source(LMFeedAnalytics.Source.UNIVERSAL_FEED)
            .attachmentType(LINK)
            .linkOGTagsViewData(linkOGTags)
            .source(LMFeedAnalytics.Source.UNIVERSAL_FEED)
            .isAdmin(initiateViewModel.isAdmin.value ?: false)
            .build()
        startCreatePostActivity(createPostExtras)
    }

    // starts post creation process as per the type if attachment selected
    private fun startPostCreation(attachmentType: Int) {
        // sends post creation started event
        viewModel.sendPostCreationStartedEvent()

        when (attachmentType) {
            VIDEO -> {
                initiateMediaPicker(listOf(com.likeminds.feedsx.media.model.VIDEO))
            }

            LINK -> {
                LMFeedLinkResourceDialogFragment.showDialog(childFragmentManager)
            }

            ARTICLE -> {
                val createPostExtras = LMFeedCreatePostExtras.Builder()
                    .attachmentType(ARTICLE)
                    .source(LMFeedAnalytics.Source.UNIVERSAL_FEED)
                    .isAdmin(initiateViewModel.isAdmin.value ?: false)
                    .build()
                startCreatePostActivity(createPostExtras)
            }

            DOCUMENT -> {
                initiateMediaPicker(listOf(PDF))
            }
        }
    }

    // starts create post activity with the required extras
    private fun startCreatePostActivity(createPostExtras: LMFeedCreatePostExtras) {
        destroyAutoPlayer("startCreatePostActivity")
        val intent = LMFeedCreatePostActivity.getIntent(
            requireContext(),
            createPostExtras
        )
        createPostLauncher.launch(intent)
    }

    // triggers gallery launcher for (IMAGE)/(VIDEO)/(IMAGE & VIDEO)
    private fun initiateMediaPicker(list: List<String>) {
        val extras = MediaPickerExtras.Builder()
            .mediaTypes(list)
            .build()
        val intent = LMFeedMediaPickerActivity.getIntent(requireContext(), extras)
        galleryLauncher.launch(intent)
    }

    /**
     * Adapter Util Block
     **/

    //get index and post from the adapter using postId
    private fun getIndexAndPostFromAdapter(postId: String): Pair<Int, PostViewData>? {
        val index = mPostAdapter.items().indexOfFirst {
            (it is PostViewData) && (it.id == postId)
        }

        if (index == -1) {
            return null
        }

        val post = getPostFromAdapter(index)

        return Pair(index, post)
    }

    //get post from the adapter using index
    private fun getPostFromAdapter(position: Int): PostViewData {
        return mPostAdapter.items()[position] as PostViewData
    }

    /**
     * Scroll to a position with offset from the top header
     * @param position Index of the item to scroll to
     */
    private fun scrollToPositionWithOffset(position: Int) {
        binding.recyclerView.post {
            val px = (ViewUtils.dpToPx(75) * 1.5).toInt()
            (binding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
                position,
                px
            )
        }
    }

    // removes the player and destroys the [postVideoAutoPlayHelper]
    private fun destroyAutoPlayer(source: String = "nothing") {
        Log.d("PUI", "destroyAutoPlayer: $source")
        if (::postVideoAutoPlayHelper.isInitialized) {
            postVideoAutoPlayHelper.detachScrollListenerForVideo()
            postVideoAutoPlayHelper.destroy(source)
        }
    }

    override fun doCleanup() {
        super.doCleanup()
        destroyAutoPlayer("doCleanup")
    }
}