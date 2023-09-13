package com.likeminds.feedsx.post.create.view

import android.app.Activity
import android.graphics.Color
import android.text.TextWatcher
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.*
import com.likeminds.feedsx.*
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedFragmentCreatePostBinding
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.VideoPreviewAutoPlayHelper
import com.likeminds.feedsx.post.create.model.CreatePostExtras
import com.likeminds.feedsx.post.create.util.CreatePostListener
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
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.link.util.LinkUtil
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.feedsx.utils.membertagging.view.LMFeedMemberTaggingView
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class LMFeedCreatePostFragment :
    BaseFragment<LmFeedFragmentCreatePostBinding, CreatePostViewModel>(),
    CreatePostListener {

    @Inject
    lateinit var initiateViewModel: InitiateViewModel

    @Inject
    lateinit var helperViewModel: HelperViewModel

    @Inject
    lateinit var userPreferences: LMFeedUserPreferences

    private lateinit var createPostExtras: CreatePostExtras

    private var selectedMediaUris: ArrayList<SingleUriData> = arrayListOf()
    private var ogTags: LinkOGTagsViewData? = null
    private var multiMediaAdapter: CreatePostMultipleMediaAdapter? = null
    private var documentsAdapter: CreatePostDocumentsAdapter? = null
    private lateinit var etPostTextChangeListener: TextWatcher
    private lateinit var memberTagging: LMFeedMemberTaggingView
    private val videoPreviewAutoPlayHelper by lazy {
        VideoPreviewAutoPlayHelper.getInstance()
    }
    override val useSharedViewModel: Boolean
        get() = true

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
        const val TYPE_OF_ATTACHMENT_CLICKED = "image, video"
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

    override fun setUpViews() {
        super.setUpViews()
        fetchUserFromDB()
        initMemberTaggingView()
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
        val createPostActivity = requireActivity() as LMFeedCreatePostActivity
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

    // handles the logic to show the type of post
    private fun showPostMedia() {
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

            else -> {
                val text = binding.etPostContent.text?.trim()
                if (selectedMediaUris.size == 0 && text != null) {
                    showLinkPreview(text.toString())
                } else {
                    clearPreviewLink()
                }
                handlePostButton(clickable = !text.isNullOrEmpty())
            }
        }
    }

    // shows attached video in single video post type
    private fun showAttachedVideo() {
        handlePostButton(clickable = true)
        binding.apply {
            singleVideoAttachment.root.show()
            singleImageAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.hide()
            val layoutSingleVideoPost = singleVideoAttachment.layoutSingleVideoPost
            videoPreviewAutoPlayHelper.playVideo(
                layoutSingleVideoPost.videoPost,
                layoutSingleVideoPost.pbVideoLoader,
                selectedMediaUris.first().uri
            )

            layoutSingleVideoPost.ivCrossVideo.setOnClickListener {
                selectedMediaUris.clear()
                singleVideoAttachment.root.hide()
                val text = etPostContent.text?.trim()
                handlePostButton(clickable = !text.isNullOrEmpty())
                videoPreviewAutoPlayHelper.removePlayer()
            }
        }
    }

    // shows attached image in single image post type
    private fun showAttachedImage() {
        handlePostButton(clickable = true)
        binding.apply {
            singleImageAttachment.root.show()
            singleVideoAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.hide()
            multipleMediaAttachment.root.hide()
            singleImageAttachment.layoutSingleImagePost.ivCrossImage.setOnClickListener {
                selectedMediaUris.clear()
                singleImageAttachment.root.hide()
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

    // shows document recycler view with attached files
    private fun showAttachedDocuments() {
        handlePostButton(clickable = true)
        binding.apply {
            singleVideoAttachment.root.hide()
            singleImageAttachment.root.hide()
            linkPreview.root.hide()
            documentsAttachment.root.show()
            multipleMediaAttachment.root.hide()
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
                documentsAdapter = CreatePostDocumentsAdapter(this@LMFeedCreatePostFragment)
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
        videoPreviewAutoPlayHelper.removePlayer()
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
            ivCrossLink.setOnClickListener {
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

    // handles Post Done button click-ability
    private fun handlePostButton(
        clickable: Boolean,
        showProgress: Boolean? = null
    ) {
        val createPostActivity = requireActivity() as LMFeedCreatePostActivity
        createPostActivity.binding.apply {
            if (showProgress == true) {
                pbPosting.show()
                tvPostDone.hide()
            } else {
                pbPosting.hide()
                if (clickable) {
                    tvPostDone.isClickable = true
                    tvPostDone.setTextColor(LMFeedBranding.getButtonsColor())
                } else {
                    tvPostDone.isClickable = false
                    tvPostDone.setTextColor(Color.parseColor("#666666"))
                }
            }
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
            videoPreviewAutoPlayHelper.removePlayer()
        }
        showPostMedia()
    }
}