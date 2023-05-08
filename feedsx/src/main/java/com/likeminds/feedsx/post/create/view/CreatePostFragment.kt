package com.likeminds.feedsx.post.create.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.likeminds.feedsx.InitiateViewModel
import com.likeminds.feedsx.LMAnalytics
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.databinding.FragmentCreatePostBinding
import com.likeminds.feedsx.databinding.ItemCreatePostSingleVideoBinding
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.DraftVideoAutoPlayHelper
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.view.MediaPickerActivity
import com.likeminds.feedsx.media.view.MediaPickerActivity.Companion.ARG_MEDIA_PICKER_RESULT
import com.likeminds.feedsx.post.create.util.CreatePostListener
import com.likeminds.feedsx.post.create.view.CreatePostActivity.Companion.POST_ATTACHMENTS_LIMIT
import com.likeminds.feedsx.post.create.view.adapter.CreatePostDocumentsAdapter
import com.likeminds.feedsx.post.create.view.adapter.CreatePostMultipleMediaAdapter
import com.likeminds.feedsx.post.create.viewmodel.CreatePostViewModel
import com.likeminds.feedsx.post.edit.viewmodel.HelperViewModel
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ValueUtils.getUrlIfExist
import com.likeminds.feedsx.utils.ValueUtils.isImageValid
import com.likeminds.feedsx.utils.ViewDataConverter.convertSingleDataUri
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.customview.DataBoundViewHolder
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.link.util.LinkUtil
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.feedsx.utils.membertagging.view.MemberTaggingView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class CreatePostFragment :
    BaseFragment<FragmentCreatePostBinding, CreatePostViewModel>(),
    CreatePostListener {

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    @Inject
    lateinit var helperViewModel: HelperViewModel

    private var selectedMediaUris: ArrayList<SingleUriData> = arrayListOf()
    private var ogTags: LinkOGTagsViewData? = null

    private var multiMediaAdapter: CreatePostMultipleMediaAdapter? = null
    private var documentsAdapter: CreatePostDocumentsAdapter? = null

    private lateinit var etPostTextChangeListener: TextWatcher

    private lateinit var memberTagging: MemberTaggingView
    private var source = ""

    private val draftVideoAutoPlayHelper = DraftVideoAutoPlayHelper.getInstance()

    override val useSharedViewModel: Boolean
        get() = true

    override fun getViewModelClass(): Class<CreatePostViewModel> {
        return CreatePostViewModel::class.java
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().createPostComponent()?.inject(this)
    }

    override fun getViewBinding(): FragmentCreatePostBinding {
        return FragmentCreatePostBinding.inflate(layoutInflater)
    }

    companion object {
        const val TAG = "CreatePostFragment"
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(CreatePostActivity.SOURCE_EXTRA) == null) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        source = arguments?.getString(CreatePostActivity.SOURCE_EXTRA)
            ?: throw emptyExtrasException(TAG)
        checkForSource()
    }

    //to check for source of the follow trigger
    private fun checkForSource() {
        //if source is notification, then call initiate first in the background
        if (source == LMAnalytics.Source.NOTIFICATION) {
            initiateViewModel.initiateUser()
        }
    }

    override fun setUpViews() {
        super.setUpViews()

        fetchUserFromDB()
        initMemberTaggingView()
        initAddAttachmentsView()
        initPostContentTextListener()
        initPostDoneListener()
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
            initAuthorFrame(it)
        }

        // observes decodeUrlResponse and returns link ogTags
        helperViewModel.decodeUrlResponse.observe(viewLifecycleOwner) { ogTags ->
            this.ogTags = ogTags
            initLinkView(ogTags)
        }

        // observes addPostResponse, once post is created
        viewModel.postAdded.observe(viewLifecycleOwner) { postAdded ->
            requireActivity().apply {
                if (postAdded) {
                    // post is already posted
                    setResult(Activity.RESULT_OK)
                } else {
                    // post is stored in db, now upload it from [FeedFragment]
                    setResult(CreatePostActivity.RESULT_UPLOAD_POST)
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
                    handlePostButton(clickable = true, showProgress = false)
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

        helperViewModel.errorEventFlow.onEach { response ->
            when (response) {
                is HelperViewModel.ErrorMessageEvent.DecodeUrl -> {
                    val postText = binding.etPostContent.text.toString()
                    val link = postText.getUrlIfExist()
                    if (link != ogTags?.url) {
                        clearPreviewLink()
                    }
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
                    LMBranding.getTextLinkColor()
                )
                .build()
        )
        memberTagging.addListener(object : MemberTaggingViewListener {
            override fun onMemberTagged(user: UserTagViewData) {
                // sends user tagged event
                helperViewModel.sendUserTagEvent(
                    user.userUniqueId,
                    memberTagging.getTaggedMemberCount()
                )
            }

            override fun callApi(page: Int, searchName: String) {
                helperViewModel.getMembersForTagging(page, searchName)
            }
        })
    }

    // adds text watcher on post content edit text
    @SuppressLint("ClickableViewAccessibility")
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun initPostContentTextListener() {
        binding.etPostContent.apply {
            /**
             * As the scrollable edit text is inside a scroll view,
             * this touch listener handles the scrolling of the edit text.
             * When the edit text is touched and has focus then it disables scroll of scroll-view.
             */
            setOnTouchListener(OnTouchListener { v, event ->
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

            // text watcher with debounce to add delay in api calls for ogTags
            textChanges()
                .debounce(500)
                .distinctUntilChanged()
                .onEach {
                    val text = it?.toString()?.trim()
                    if (selectedMediaUris.isNotEmpty()) return@onEach
                    if (!text.isNullOrEmpty()) {
                        showPostMedia()
                    }
                }
                .launchIn(lifecycleScope)

            // text watcher to handlePostButton click-ability
            addTextChangedListener {
                val text = it?.toString()?.trim()
                if (text.isNullOrEmpty()) {
                    clearPreviewLink()
                    if (selectedMediaUris.isEmpty()) {
                        handlePostButton(clickable = false)
                    } else {
                        handlePostButton(clickable = true)
                    }
                } else {
                    handlePostButton(clickable = true)
                }
            }
        }
    }

    // initializes post done button click listener
    private fun initPostDoneListener() {
        val createPostActivity = requireActivity() as CreatePostActivity
        createPostActivity.binding.apply {
            tvPostDone.setOnClickListener {
                val text = binding.etPostContent.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                if (selectedMediaUris.isNotEmpty()) {
                    handlePostButton(clickable = true, showProgress = true)
                    viewModel.addPost(
                        requireContext(),
                        updatedText,
                        selectedMediaUris,
                        ogTags
                    )
                } else if (updatedText.isNotEmpty()) {
                    handlePostButton(clickable = true, showProgress = true)
                    viewModel.addPost(
                        requireContext(),
                        updatedText,
                        ogTags = ogTags
                    )
                }
            }
        }
    }

    // initializes click listeners on add attachment layouts
    private fun initAddAttachmentsView() {
        binding.apply {
            layoutAttachFiles.setOnClickListener {
                // sends clicked on attachment event for file
                viewModel.sendClickedOnAttachmentEvent("file")
                val extra = MediaPickerExtras.Builder()
                    .mediaTypes(listOf(PDF))
                    .allowMultipleSelect(true)
                    .build()
                val intent = MediaPickerActivity.getIntent(requireContext(), extra)
                documentLauncher.launch(intent)
            }

            layoutAddImage.setOnClickListener {
                // sends clicked on attachment event for photo
                viewModel.sendClickedOnAttachmentEvent("photo")
                initiateMediaPicker(listOf(IMAGE))
            }

            layoutAddVideo.setOnClickListener {
                // sends clicked on attachment event for video
                viewModel.sendClickedOnAttachmentEvent("video")
                initiateMediaPicker(listOf(VIDEO))
            }
        }
    }

    // sets data to the author frame
    private fun initAuthorFrame(user: UserViewData) {
        binding.authorFrame.apply {
            tvCreatorName.text = user.name
            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                user.userUniqueId,
                creatorImage,
                showRoundImage = true,
                objectKey = user.updatedAt
            )
        }
    }

    /**
     * Adds TextWatcher to edit text with Flow operators
     * **/
    @ExperimentalCoroutinesApi
    @CheckResult
    fun EditText.textChanges(): Flow<CharSequence?> {
        return callbackFlow<CharSequence?> {
            etPostTextChangeListener = object : TextWatcher {
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
            addTextChangedListener(etPostTextChangeListener)
            awaitClose { removeTextChangedListener(etPostTextChangeListener) }
        }.onStart { emit(text) }
    }

    // triggers gallery launcher for (IMAGE)/(VIDEO)/(IMAGE & VIDEO)
    private fun initiateMediaPicker(list: List<String>) {
        val extras = MediaPickerExtras.Builder()
            .mediaTypes(list)
            .allowMultipleSelect(true)
            .build()

        val intent = MediaPickerActivity.getIntent(requireContext(), extras)
        galleryLauncher.launch(intent)
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
        selectedMediaUris.addAll(data)
        showPostMedia()
    }

    private fun onMediaPickedFromGallery(data: Intent?) {
        val uris = MediaUtils.getExternalIntentPickerUris(data)
        viewModel.fetchUriDetails(requireContext(), uris) {
            val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                requireContext(), it
            )
            // sends media attached event with media type and count
            viewModel.sendMediaAttachedEvent(mediaUris)
            selectedMediaUris.addAll(mediaUris)
            if (mediaUris.isNotEmpty()) {
                showPostMedia()
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
            selectedMediaUris.addAll(mediaUris)
            if (mediaUris.isNotEmpty()) {
                attachmentsLimitExceeded()
                showAttachedDocuments()
            }
        }
    }

    // handles the logic to show the type of post
    private fun showPostMedia() {
        attachmentsLimitExceeded()
        when {
            selectedMediaUris.size >= 1 && MediaType.isPDF(selectedMediaUris.first().fileType) -> {
                ogTags = null
                showAttachedDocuments()
            }
            selectedMediaUris.size == 1 && MediaType.isImage(selectedMediaUris.first().fileType) -> {
                ogTags = null
                showAttachedImage()
            }
            selectedMediaUris.size == 1 && MediaType.isVideo(selectedMediaUris.first().fileType) -> {
                ogTags = null
                showAttachedVideo()
            }
            selectedMediaUris.size >= 1 -> {
                ogTags = null
                showMultiMediaAttachments()
            }
            else -> {
                val text = binding.etPostContent.text?.trim()
                if (selectedMediaUris.size == 0 && text != null) {
                    showLinkPreview(text.toString())
                } else {
                    clearPreviewLink()
                }
                handlePostButton(clickable = !text.isNullOrEmpty())
                handleAddAttachmentLayouts(true)
            }
        }
    }

    // shows attached video in single video post type
    private fun showAttachedVideo() {
        handleAddAttachmentLayouts(false)
        handlePostButton(clickable = true)
        binding.apply {
            singleVideoAttachment.root.show()
            singleImageAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.hide()
            singleVideoAttachment.btnAddMore.setOnClickListener {
                // sends clicked on attachment event for image and video
                viewModel.sendClickedOnAttachmentEvent("image, video")
                initiateMediaPicker(listOf(IMAGE, VIDEO))
            }

            val layoutSingleVideoPost = singleVideoAttachment.layoutSingleVideoPost
            draftVideoAutoPlayHelper.playVideo(
                layoutSingleVideoPost.videoPost,
                selectedMediaUris.first().uri
            )

            layoutSingleVideoPost.ivCross.setOnClickListener {
                selectedMediaUris.clear()
                singleVideoAttachment.root.hide()
                handleAddAttachmentLayouts(true)
                val text = etPostContent.text?.trim()
                handlePostButton(clickable = !text.isNullOrEmpty())
                draftVideoAutoPlayHelper.removePlayer()
            }
        }
    }

    // shows attached image in single image post type
    private fun showAttachedImage() {
        handleAddAttachmentLayouts(false)
        handlePostButton(clickable = true)
        binding.apply {
            singleImageAttachment.root.show()
            singleVideoAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.hide()
            singleImageAttachment.btnAddMore.setOnClickListener {
                // sends clicked on attachment event for image and video
                viewModel.sendClickedOnAttachmentEvent("image, video")
                initiateMediaPicker(listOf(IMAGE, VIDEO))
            }
            singleImageAttachment.layoutSingleImagePost.ivCross.setOnClickListener {
                selectedMediaUris.clear()
                singleImageAttachment.root.hide()
                handleAddAttachmentLayouts(true)
                val text = etPostContent.text?.trim()
                handlePostButton(clickable = !text.isNullOrEmpty())
            }
            // gets the shimmer drawable for placeholder
            val shimmerDrawable = ViewUtils.getShimmer()

            ImageBindingUtil.loadImage(
                singleImageAttachment.layoutSingleImagePost.ivSingleImagePost,
                selectedMediaUris.first().uri,
                placeholder = shimmerDrawable
            )
        }
    }

    // shows view pager with multiple media
    private fun showMultiMediaAttachments() {
        handleAddAttachmentLayouts(false)
        handlePostButton(clickable = true)
        binding.apply {
            singleImageAttachment.root.hide()
            singleVideoAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.show()
            multipleMediaAttachment.buttonColor = LMBranding.getButtonsColor()
            multipleMediaAttachment.btnAddMore.visibility =
                if (selectedMediaUris.size >= POST_ATTACHMENTS_LIMIT) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            multipleMediaAttachment.btnAddMore.setOnClickListener {
                // sends clicked on attachment event for image and video
                viewModel.sendClickedOnAttachmentEvent("image, video")
                initiateMediaPicker(listOf(IMAGE, VIDEO))
            }

            val attachments = selectedMediaUris.map {
                convertSingleDataUri(it)
            }

            val viewPager = multipleMediaAttachment.viewpagerMultipleMedia
            multiMediaAdapter = CreatePostMultipleMediaAdapter(this@CreatePostFragment)
            viewPager.adapter = multiMediaAdapter
            multipleMediaAttachment.dotsIndicator.setViewPager2(viewPager)
            multiMediaAdapter!!.replace(attachments)

            viewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    // processes the current video whenever view pager's page is changed
                    val createPostSingleVideoBinding =
                        ((viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(position) as? DataBoundViewHolder<*>)
                            ?.binding as? ItemCreatePostSingleVideoBinding

                    if (createPostSingleVideoBinding == null) {
                        // in case the item is not a video
                        draftVideoAutoPlayHelper.removePlayer()
                    } else {
                        // processes the current video item
                        draftVideoAutoPlayHelper.playVideo(
                            createPostSingleVideoBinding.videoPost,
                            selectedMediaUris[position].uri
                        )
                    }
                }
            })
        }
    }

    // shows document recycler view with attached files
    private fun showAttachedDocuments() {
        handleAddAttachmentLayouts(false)
        handlePostButton(clickable = true)
        binding.apply {
            singleVideoAttachment.root.hide()
            singleImageAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.show()
            multipleMediaAttachment.root.hide()
            documentsAttachment.btnAddMore.visibility =
                if (selectedMediaUris.size >= POST_ATTACHMENTS_LIMIT) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
            documentsAttachment.btnAddMore.setOnClickListener {
                // sends clicked on attachment event for file
                viewModel.sendClickedOnAttachmentEvent("file")
                initiateMediaPicker(listOf(PDF))
            }

            val attachments = selectedMediaUris.map {
                convertSingleDataUri(it)
            }

            if (documentsAdapter == null) {
                // item decorator to add spacing between items
                val dividerItemDecorator =
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                dividerItemDecorator.setDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.document_item_divider
                    ) ?: return
                )
                documentsAdapter = CreatePostDocumentsAdapter(this@CreatePostFragment)
                documentsAttachment.rvDocuments.apply {
                    adapter = documentsAdapter
                    layoutManager = LinearLayoutManager(context)
                    addItemDecoration(dividerItemDecorator)
                }
            }
            documentsAdapter!!.replace(attachments)
        }
    }

    override fun onResume() {
        super.onResume()
        showPostMedia()
    }

    override fun onPause() {
        super.onPause()
        draftVideoAutoPlayHelper.removePlayer()
    }

    // shows link preview for link post type
    private fun showLinkPreview(text: String?) {
        binding.linkPreview.apply {
            if (text.isNullOrEmpty()) {
                clearPreviewLink()
                return
            }
            val link = text.getUrlIfExist()
            if (ogTags != null && link.equals(ogTags?.url)) {
                return
            }
            if (!link.isNullOrEmpty()) {
                if (link == ogTags?.url) {
                    return
                }
                clearPreviewLink()
                helperViewModel.decodeUrl(link)
            } else {
                clearPreviewLink()
            }
        }
    }

    // renders data in the link view
    private fun initLinkView(data: LinkOGTagsViewData) {
        val link = data.url ?: ""
        // sends link attached event with the link
        helperViewModel.sendLinkAttachedEvent(link)
        binding.linkPreview.apply {
            root.show()

            val isImageValid = data.image.isImageValid()
            ivLink.isVisible = isImageValid
            LinkUtil.handleLinkPreviewConstraints(
                this,
                isImageValid
            )

            tvLinkTitle.text = if (data.title?.isNotBlank() == true) {
                data.title
            } else {
                root.context.getString(R.string.link)
            }
            tvLinkDescription.isVisible = !data.description.isNullOrEmpty()
            tvLinkDescription.text = data.description

            if (isImageValid) {
                ImageBindingUtil.loadImage(
                    ivLink,
                    data.image,
                    placeholder = R.drawable.ic_link_primary_40dp,
                    cornerRadius = 8
                )
            }

            tvLinkUrl.text = data.url?.lowercase(Locale.getDefault()) ?: ""
            ivCross.setOnClickListener {
                binding.etPostContent.removeTextChangedListener(etPostTextChangeListener)
                clearPreviewLink()
            }
        }
    }

    // clears link preview
    private fun clearPreviewLink() {
        ogTags = null
        binding.linkPreview.apply {
            root.hide()
        }
    }

    // handles visibility of add attachment layouts
    private fun handleAddAttachmentLayouts(show: Boolean) {
        binding.groupAddAttachments.isVisible = show
    }

    // handles Post Done button click-ability
    private fun handlePostButton(
        clickable: Boolean,
        showProgress: Boolean? = null
    ) {
        val createPostActivity = requireActivity() as CreatePostActivity
        createPostActivity.binding.apply {
            if (showProgress == true) {
                pbPosting.show()
                tvPostDone.hide()
            } else {
                pbPosting.hide()
                if (clickable) {
                    tvPostDone.isClickable = true
                    tvPostDone.setTextColor(LMBranding.getButtonsColor())
                } else {
                    tvPostDone.isClickable = false
                    tvPostDone.setTextColor(Color.parseColor("#666666"))
                }
            }
        }
    }

    // shows toast and removes extra items if attachments limit is exceeded
    private fun attachmentsLimitExceeded() {
        if (selectedMediaUris.size > 10) {
            ViewUtils.showErrorMessageToast(
                requireContext(), requireContext().resources.getQuantityString(
                    R.plurals.you_can_select_upto_x_items,
                    POST_ATTACHMENTS_LIMIT,
                    POST_ATTACHMENTS_LIMIT
                )
            )
            val size = selectedMediaUris.size
            selectedMediaUris.subList(POST_ATTACHMENTS_LIMIT, size).clear()
        }
    }

    // triggered when a document/media from view pager is removed
    override fun onMediaRemoved(position: Int, mediaType: String) {
        selectedMediaUris.removeAt(position)
        if (mediaType == PDF) {
            documentsAdapter?.removeIndex(position)
            if (documentsAdapter?.itemCount == 0) binding.documentsAttachment.root.hide()
        } else {
            multiMediaAdapter?.removeIndex(position)
            draftVideoAutoPlayHelper.removePlayer()
        }
        showPostMedia()
    }

    // launcher to handle gallery (IMAGE/VIDEO) intent
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(ARG_MEDIA_PICKER_RESULT)
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

    // launcher to handle document (PDF) intent
    private val documentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data =
                    result.data?.extras?.getParcelable<MediaPickerResult>(
                        ARG_MEDIA_PICKER_RESULT
                    )
                checkMediaPickedResult(data)
            }
        }
}