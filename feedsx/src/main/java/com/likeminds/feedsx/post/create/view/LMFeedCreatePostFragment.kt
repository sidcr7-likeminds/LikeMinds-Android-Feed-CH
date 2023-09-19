package com.likeminds.feedsx.post.create.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.likeminds.feedsx.*
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedFragmentCreatePostBinding
import com.likeminds.feedsx.feed.view.LMFeedFragment
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.util.VideoPreviewAutoPlayHelper
import com.likeminds.feedsx.media.view.LMFeedImageCropFragment
import com.likeminds.feedsx.media.view.LMFeedMediaPickerActivity
import com.likeminds.feedsx.post.create.model.CreatePostExtras
import com.likeminds.feedsx.post.create.model.RemoveDialogExtras
import com.likeminds.feedsx.post.create.viewmodel.CreatePostViewModel
import com.likeminds.feedsx.post.edit.viewmodel.HelperViewModel
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ValueUtils.getUrlIfExist
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.feedsx.utils.membertagging.view.LMFeedMemberTaggingView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class LMFeedCreatePostFragment :
    BaseFragment<LmFeedFragmentCreatePostBinding, CreatePostViewModel>(),
    LMFeedDiscardResourceDialog.DiscardResourceDialogListener,
    LMFeedRemoveAttachmentDialogFragment.RemoveAttachmentDialogListener {

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    @Inject
    lateinit var helperViewModel: HelperViewModel

    @Inject
    lateinit var userPreferences: LMFeedUserPreferences

    private lateinit var createPostExtras: CreatePostExtras

    private lateinit var etLinkTextChangeListener: TextWatcher

    private var onBehalfOfUUID: String? = null
    private var loggedInUserUUID: String = ""

    private var user: UserViewData? = null

    private var selectedMediaUris: ArrayList<SingleUriData> = arrayListOf()
    private var ogTags: LinkOGTagsViewData? = null
    private lateinit var memberTagging: LMFeedMemberTaggingView
    private val videoPreviewAutoPlayHelper by lazy {
        VideoPreviewAutoPlayHelper.getInstance()
    }
    private var discardResourceDialog: LMFeedDiscardResourceDialog? = null
    private var removeAttachmentDialogFragment: LMFeedRemoveAttachmentDialogFragment? = null

    override val useSharedViewModel: Boolean
        get() = true

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

    private val selectAuthorLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                user = ExtrasUtil.getParcelable(
                    result.data?.extras,
                    LMFeedSelectAuthorFragment.ARG_SELECT_AUTHOR_RESULT,
                    UserViewData::class.java
                ) ?: UserViewData.Builder().build()
                onAuthorChanged()
            }
        }

    override fun getViewModelClass(): Class<CreatePostViewModel> {
        return CreatePostViewModel::class.java
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().createPostComponent()?.inject(this)
    }

    override fun getViewBinding(): LmFeedFragmentCreatePostBinding {
        return LmFeedFragmentCreatePostBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "CreatePostFragment"
        const val MIN_ARTICLE_CONTENT = 200
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(LMFeedCreatePostActivity.CREATE_POST_EXTRAS) == null) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        createPostExtras = ExtrasUtil.getParcelable(
            arguments,
            LMFeedCreatePostActivity.CREATE_POST_EXTRAS,
            CreatePostExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
        checkForSource()
    }

    //to check for source of the follow trigger
    private fun checkForSource() {
        //if source is notification, then call initiate first in the background
        if (createPostExtras.source == LMFeedAnalytics.Source.NOTIFICATION) {
            initiateViewModel.initiateUser(
                requireContext(),
                userPreferences.getApiKey(),
                userPreferences.getUserName(),
                userPreferences.getUserUniqueId(),
                userPreferences.getIsGuest()
            )
        }
    }

    override fun handleResultListener() {
        super.handleResultListener()

        setFragmentResultListener(LMFeedImageCropFragment.REQUEST_KEY) { _, bundle ->
            val singleUriData =
                ExtrasUtil.getParcelable(
                    bundle,
                    LMFeedImageCropFragment.BUNDLE_ARG_URI,
                    SingleUriData::class.java
                ) ?: return@setFragmentResultListener

            selectedMediaUris.add(singleUriData)
            initAuthorFrame()
            showPostMedia()
        }
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
        initEditTextListener()
        fetchUserFromDB()
        initMemberTaggingView()
        initPostDoneListener()
    }

    // initializes the view as per the selected post type
    private fun initView() {
        binding.apply {
            createPostExtras.attachmentUri?.let { selectedMediaUris.add(it) }
            ogTags = createPostExtras.linkOGTagsViewData

            if (createPostExtras.isAdmin) {
                authorFrame.ivChangeAuthor.show()
            } else {
                authorFrame.ivChangeAuthor.hide()
            }

            ViewUtils.getMandatoryAsterisk(
                getString(R.string.add_title),
                etPostTitle
            )
            etPostTitle.setHintTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.dark_grey
                )
            )
            initPostTextContentTouchListener()
            initLinkListener()
            showPostMedia()
            initClickListeners()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initPostTextContentTouchListener() {
        binding.etPostContent.apply {

            /**
             * As the scrollable edit text is inside a scroll view,
             * this touch listener handles the scrolling of the edit text.
             * When the edit text is touched and has focus then it disables scroll of scroll-view.
             */
            setOnTouchListener(View.OnTouchListener { v, event ->
                if (hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun initLinkListener() {
        // text watcher with debounce to add delay in api calls for ogTags
        binding.etPostLink.textChanges()
            .debounce(500)
            .distinctUntilChanged()
            .onEach {
                val text = binding.etPostLink.text?.trim().toString()
                val link = text.getUrlIfExist()

                if (!link.isNullOrEmpty()) {
                    helperViewModel.decodeUrl(link)
                }
            }
            .launchIn(lifecycleScope)
    }

    // initializes a listener to edit text
    private fun initEditTextListener() {
        binding.etPostTitle.doAfterTextChanged {
            showPostMedia()
        }

        if (createPostExtras.attachmentType == ARTICLE) {
            binding.etPostContent.doAfterTextChanged {
                showPostMedia()
            }
        }
    }

    // initializes click listeners
    private fun initClickListeners() {
        binding.apply {
            cvArticleImage.setOnClickListener {
                initiateMediaPicker(listOf(com.likeminds.feedsx.media.model.IMAGE))
            }

            ivDeleteArticle.setOnClickListener {
                val removeExtras = RemoveDialogExtras.Builder()
                    .title(getString(R.string.remove_article_banner))
                    .description(getString(R.string.are_you_sure_you_want_to_remove_the_article_banner))
                    .build()
                showRemoveDialog(removeExtras)
            }

            ivDeleteMedia.setOnClickListener {
                val description =
                    if (createPostExtras.attachmentType == com.likeminds.feedsx.posttypes.model.VIDEO) {
                        getString(R.string.are_you_sure_you_want_to_remove_the_attached_video)
                    } else {
                        getString(R.string.are_you_sure_you_want_to_remove_the_attached_file)
                    }
                val removeExtras = RemoveDialogExtras.Builder()
                    .title(getString(R.string.remove_attachment))
                    .description(description)
                    .build()
                showRemoveDialog(removeExtras)
            }

            cvAddMedia.setOnClickListener {
                if (createPostExtras.attachmentType == com.likeminds.feedsx.posttypes.model.VIDEO) {
                    initiateMediaPicker(listOf(com.likeminds.feedsx.media.model.VIDEO))
                }

                if (createPostExtras.attachmentType == com.likeminds.feedsx.posttypes.model.DOCUMENT) {
                    initiateMediaPicker(listOf(com.likeminds.feedsx.media.model.PDF))
                }
            }

            authorFrame.ivChangeAuthor.setOnClickListener {
                selectAuthorLauncher.launch(LMFeedSelectAuthorActivity.getIntent(requireContext()))
            }
        }
    }

    private fun onAuthorChanged() {
        if (loggedInUserUUID != user?.sdkClientInfoViewData?.uuid) {
            onBehalfOfUUID = user?.sdkClientInfoViewData?.uuid
        }
        initAuthorFrame()
    }

    // shows media remove dialog
    private fun showRemoveDialog(removeDialogExtras: RemoveDialogExtras) {
        removeAttachmentDialogFragment = LMFeedRemoveAttachmentDialogFragment.showDialog(
            childFragmentManager,
            removeDialogExtras
        )
    }

    // triggers gallery launcher for (IMAGE)/(VIDEO)/(IMAGE & VIDEO)
    private fun initiateMediaPicker(list: List<String>) {
        val extras = MediaPickerExtras.Builder()
            .mediaTypes(list)
            .allowMultipleSelect(true)
            .build()
        val intent = LMFeedMediaPickerActivity.getIntent(requireContext(), extras)
        galleryLauncher.launch(intent)
    }

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
            if (data.first().fileType == com.likeminds.feedsx.media.model.IMAGE) {
                val imageCropExtras = ImageCropExtras.Builder()
                    .singleUriData(data.first())
                    .cropWidth(16)
                    .cropHeight(9)
                    .build()

                findNavController().navigate(
                    LMFeedCreatePostFragmentDirections.actionFragmentCreatePostToImageCropFragment(
                        imageCropExtras
                    )
                )
            } else if (checkForValidAttachment(data.first())) {
                selectedMediaUris.addAll(data)
                showPostMedia()
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
                if (mediaUris.first().fileType == com.likeminds.feedsx.media.model.IMAGE) {
                    val imageCropExtras = ImageCropExtras.Builder()
                        .singleUriData(mediaUris.first())
                        .cropWidth(16)
                        .cropHeight(9)
                        .build()

                    findNavController().navigate(
                        LMFeedCreatePostFragmentDirections.actionFragmentCreatePostToImageCropFragment(
                            imageCropExtras
                        )
                    )
                } else if (checkForValidAttachment(mediaUris.first())) {
                    selectedMediaUris.addAll(mediaUris)
                    showPostMedia()
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
                if (checkForValidAttachment(mediaUris.first())) {
                    selectedMediaUris.addAll(mediaUris)
                    showPostMedia()
                }
            }
        }
    }

    // checks if the selected attachment is allowed or not
    private fun checkForValidAttachment(uri: SingleUriData?): Boolean {
        return when (uri?.fileType) {
            com.likeminds.feedsx.media.model.VIDEO -> {
                if (uri.duration != null && uri.duration > LMFeedFragment.VIDEO_DURATION_LIMIT) {
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

            PDF -> {
                if (uri.size > LMFeedFragment.PDF_SIZE_LIMIT) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.pdf_must_be_less_than_8_MB),
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

    // fetches user data from local db
    private fun fetchUserFromDB() {
        helperViewModel.fetchUserFromDB()
    }

    // observes data
    override fun observeData() {
        super.observeData()
        // observes error message
        observeErrors()
        observeMembersTaggingList()
        // observes userData and initializes the user view
        helperViewModel.userData.observe(viewLifecycleOwner) {
            loggedInUserUUID = it.sdkClientInfoViewData.uuid
            if (user == null) {
                user = it
                initAuthorFrame()
            }
        }
        // observes decodeUrlResponse and returns link ogTags
        helperViewModel.decodeUrlResponse.observe(viewLifecycleOwner) { ogTags ->
            this.ogTags = ogTags
            showPostMedia()
        }
        // observes addPostResponse, once post is created
        viewModel.postAdded.observe(viewLifecycleOwner) { postAdded ->
            requireActivity().apply {
                handleProgressBar(false)
                if (postAdded) {
                    // post is already posted
                    setResult(Activity.RESULT_OK)
                } else {
                    // post is stored in db, now upload it from [FeedFragment]
                    setResult(LMFeedCreatePostActivity.RESULT_UPLOAD_POST)
                }
                finish()
            }
        }
    }

    /**
     * Observes for member tagging list, This is a live observer which will update itself on addition of new members
     * [taggingData] contains first -> page called in api
     * second -> Community Members and Groups
     */
    private fun observeMembersTaggingList() {
        helperViewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    // observes error events
    private fun observeErrors() {
        viewModel.errorEventFlow.onEach { response ->
            when (response) {
                is CreatePostViewModel.ErrorMessageEvent.AddPost -> {
                    handleProgressBar(false)
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

            }
        }.observeInLifecycle(viewLifecycleOwner)

        helperViewModel.errorEventFlow.onEach { response ->
            when (response) {
                is HelperViewModel.ErrorMessageEvent.DecodeUrl -> {
                    clearPreviewLink()
                }

                is HelperViewModel.ErrorMessageEvent.GetTaggingList -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    /**
     * initializes the [memberTaggingView] with the edit text
     * also sets listener to the [memberTaggingView]
     */
    private fun initMemberTaggingView() {
        memberTagging = binding.memberTaggingView
        memberTagging.initialize(
            MemberTaggingExtras.Builder()
                .editText(binding.etPostContent)
                .maxHeightInPercentage(0.4f)
                .color(
                    LMFeedBranding.getTextLinkColor()
                )
                .build()
        )
        memberTagging.addListener(object : MemberTaggingViewListener {
            override fun onMemberTagged(user: UserTagViewData) {
                // sends user tagged event
                helperViewModel.sendUserTagEvent(
                    user.uuid,
                    memberTagging.getTaggedMemberCount()
                )
            }

            override fun callApi(page: Int, searchName: String) {
                helperViewModel.getMembersForTagging(page, searchName)
            }
        })
    }

    // initializes post done button click listener
    private fun initPostDoneListener() {
        binding.apply {
            btnPost.setOnClickListener {
                val text = etPostContent.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                val postTitle = etPostTitle.text?.trim().toString()
                handleProgressBar(true)

                viewModel.addPost(
                    requireContext(),
                    postTitle,
                    updatedText,
                    selectedMediaUris,
                    ogTags,
                    onBehalfOfUUID
                )
            }
        }
    }

    // shows/hides progress when required
    private fun handleProgressBar(showProgress: Boolean) {
        binding.progressBar.root.isVisible = showProgress
        handlePostButton(!showProgress)
    }

    // sets data to the author frame
    private fun initAuthorFrame() {
        binding.authorFrame.apply {
            tvCreatorName.text = user?.name
            MemberImageUtil.setImage(
                user?.imageUrl,
                user?.name,
                user?.userUniqueId,
                creatorImage,
                showRoundImage = true,
                objectKey = user?.updatedAt
            )
        }
    }

    // handles the logic to show the type of post
    private fun showPostMedia() {
        when (createPostExtras.attachmentType) {
            com.likeminds.feedsx.posttypes.model.VIDEO -> {
                showAttachedMedia()
            }

            DOCUMENT -> {
                showAttachedMedia()
            }

            LINK -> {
                showLinkPreview()
            }

            ARTICLE -> {
                showAddArticle()
            }

            else -> {}
        }
    }

    // shows attached media in video/document post type
    private fun showAttachedMedia() {
        binding.apply {
            ivArticle.hide()
            cvArticleImage.hide()
            linkPreview.root.hide()
            etPostLink.hide()
            val selectedMedia = selectedMediaUris.firstOrNull()
            if (selectedMedia == null) {
                handlePostButton(visible = false)
                grpMedia.hide()
                cvAddMedia.show()
                if (createPostExtras.attachmentType == com.likeminds.feedsx.posttypes.model.VIDEO) {
                    ViewUtils.getMandatoryAsterisk(
                        getString(R.string.select_video_to_share),
                        tvAddMedia
                    )

                    ImageBindingUtil.loadImage(
                        ivAddMedia,
                        R.drawable.ic_add_video
                    )
                } else if (createPostExtras.attachmentType == com.likeminds.feedsx.posttypes.model.DOCUMENT) {
                    ViewUtils.getMandatoryAsterisk(
                        getString(R.string.select_pdf_to_share),
                        tvAddMedia
                    )

                    ImageBindingUtil.loadImage(
                        ivAddMedia,
                        R.drawable.ic_add_pdf
                    )
                }
            } else {
                val title = etPostTitle.text?.trim()
                if (title.isNullOrEmpty()) {
                    handlePostButton(visible = false)
                } else {
                    handlePostButton(visible = true)
                }
                grpMedia.show()
                cvAddMedia.hide()
                tvMediaName.text = createPostExtras.attachmentUri?.mediaName
                tvMediaSize.text =
                    getString(
                        R.string.f_MB,
                        (selectedMediaUris.firstOrNull()?.size?.div(1000000.0))
                    )
            }
        }
    }

    // shows add article view
    private fun showAddArticle() {
        binding.apply {
            cvArticleImage.show()
            val selectedMedia = selectedMediaUris.firstOrNull()
            if (selectedMedia == null) {
                handlePostButton(visible = false)
                ivArticle.hide()
                ivDeleteArticle.hide()
                llAddArticle.show()
                cvArticleImage.isClickable = true
            } else {
                val title = etPostTitle.text?.trim()
                val articleContent = etPostContent.text?.trim() ?: ""
                if (title.isNullOrEmpty() || articleContent.length < MIN_ARTICLE_CONTENT) {
                    handlePostButton(visible = false)
                } else {
                    handlePostButton(visible = true)
                }
                llAddArticle.hide()
                ivArticle.show()
                ivDeleteArticle.show()
                cvArticleImage.isClickable = false
                ImageBindingUtil.loadImage(
                    ivArticle,
                    selectedMedia.uri
                )
            }
            grpMedia.hide()
            linkPreview.root.hide()
            etPostLink.hide()
            ViewUtils.getMandatoryAsterisk(
                getString(R.string.write_something_here_min_200),
                etPostContent
            )
            ViewUtils.getMandatoryAsterisk(
                getString(R.string.add_cover_photo),
                tvAddCoverPhoto
            )
        }
    }

    override fun onResume() {
        super.onResume()
        showPostMedia()
    }

    override fun onPause() {
        super.onPause()
        videoPreviewAutoPlayHelper.removePlayer()
    }

    // shows link preview for link post type
    private fun showLinkPreview() {
        binding.linkPreview.apply {
            if (ogTags == null) {
                return
            }
            root.show()
            binding.etPostLink.hide()
            binding.etPostLink.removeTextChangedListener(etLinkTextChangeListener)
            val title = binding.etPostTitle.text?.trim()
            if (title.isNullOrEmpty()) {
                handlePostButton(visible = false)
            } else {
                handlePostButton(visible = true)
            }
            // sends link attached event with the link
            helperViewModel.sendLinkAttachedEvent(ogTags?.url ?: "")
            ImageBindingUtil.loadImage(
                ivLink,
                ogTags?.image,
                placeholder = R.drawable.ic_link_primary_40dp
            )

            tvLinkTitle.text = if (ogTags?.title?.isNotBlank() == true) {
                ogTags?.title
            } else {
                root.context.getString(R.string.link)
            }
            tvLinkDescription.isVisible = !ogTags?.description.isNullOrEmpty()
            tvLinkDescription.text = ogTags?.description
            tvLinkUrl.text = ogTags?.url?.lowercase(Locale.getDefault())

            ivDeleteLink.setOnClickListener {
                val removeDialogExtras = RemoveDialogExtras.Builder()
                    .title(getString(R.string.remove_link))
                    .description(getString(R.string.are_you_sure_you_want_to_remove_the_attached_link))
                    .build()
                showRemoveDialog(removeDialogExtras)
            }
        }
    }

    /**
     * Adds TextWatcher to edit text with Flow operators
     * **/
    @ExperimentalCoroutinesApi
    @CheckResult
    fun EditText.textChanges(): Flow<CharSequence?> {
        return callbackFlow<CharSequence?> {
            etLinkTextChangeListener = object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    (this@callbackFlow).trySend(s.toString())
                }
            }
            addTextChangedListener(etLinkTextChangeListener)
            awaitClose { removeTextChangedListener(etLinkTextChangeListener) }
        }.onStart { emit(text) }
    }

    // clears link preview
    private fun clearPreviewLink() {
        binding.apply {
            handlePostButton(visible = false)
            ogTags = null
            linkPreview.apply {
                root.hide()
            }
            ViewUtils.getMandatoryAsterisk(
                getString(R.string.share_link_resource),
                etPostLink
            )
            etPostLink.show()
            etPostLink.addTextChangedListener(etLinkTextChangeListener)
        }
    }

    // handles Post Done button visibility
    private fun handlePostButton(visible: Boolean) {
        binding.apply {
            if (visible) {
                btnPost.show()
            } else {
                btnPost.hide()
            }
        }
    }

    // shows discard resource popup
    fun openBackPressedPopup() {
        discardResourceDialog = LMFeedDiscardResourceDialog.show(childFragmentManager)
    }

    // when user clicks on discard resource
    override fun onResourceDiscarded() {
        requireActivity().finish()
    }

    // when user clicks on continue resource creation
    override fun onResourceCreationContinued() {
        discardResourceDialog?.dismiss()
    }

    // when user removes attachment
    override fun onRemoved() {
        selectedMediaUris.clear()
        showPostMedia()
        ogTags = null
        if (createPostExtras.attachmentType == LINK) {
            binding.etPostLink.text?.clear()
            clearPreviewLink()
        }
        removeAttachmentDialogFragment?.dismiss()
    }

    // when user removes attachment
    override fun onCancelled() {
        removeAttachmentDialogFragment?.dismiss()
    }
}