package com.likeminds.feedsample.post.edit.view

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsample.R
import com.likeminds.feedsample.branding.model.LMBranding
import com.likeminds.feedsample.databinding.FragmentEditPostBinding
import com.likeminds.feedsample.feed.util.PostEvent
import com.likeminds.feedsample.post.edit.model.EditPostExtras
import com.likeminds.feedsample.post.edit.view.EditPostActivity.Companion.EDIT_POST_EXTRAS
import com.likeminds.feedsample.post.edit.view.adapter.EditPostDocumentsAdapter
import com.likeminds.feedsample.post.edit.viewmodel.EditPostViewModel
import com.likeminds.feedsample.post.edit.viewmodel.HelperViewModel
import com.likeminds.feedsample.posttypes.model.*
import com.likeminds.feedsample.posttypes.util.PostTypeUtil
import com.likeminds.feedsample.posttypes.view.adapter.MultipleMediaPostAdapter
import com.likeminds.feedsample.utils.MemberImageUtil
import com.likeminds.feedsample.utils.ProgressHelper
import com.likeminds.feedsample.utils.ValueUtils.getUrlIfExist
import com.likeminds.feedsample.utils.ValueUtils.isImageValid
import com.likeminds.feedsample.utils.ViewUtils
import com.likeminds.feedsample.utils.ViewUtils.hide
import com.likeminds.feedsample.utils.ViewUtils.show
import com.likeminds.feedsample.utils.customview.BaseFragment
import com.likeminds.feedsample.utils.databinding.ImageBindingUtil
import com.likeminds.feedsample.utils.link.util.LinkUtil
import com.likeminds.feedsample.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsample.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsample.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.feedsample.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.feedsample.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.feedsample.utils.membertagging.view.MemberTaggingView
import com.likeminds.feedsample.utils.model.*
import com.likeminds.feedsample.utils.observeInLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.util.*

@AndroidEntryPoint
class EditPostFragment : BaseFragment<FragmentEditPostBinding>() {

    private val viewModel: EditPostViewModel by viewModels()
    private val helperViewModel: HelperViewModel by activityViewModels()

    private lateinit var editPostExtras: EditPostExtras

    private var fileAttachments: List<AttachmentViewData>? = null
    private var ogTags: LinkOGTagsViewData? = null

    private lateinit var etPostTextChangeListener: TextWatcher

    private lateinit var memberTagging: MemberTaggingView

    // [postPublisher] to publish changes in the post
    private val postEvent = PostEvent.getPublisher()

    override fun getViewBinding(): FragmentEditPostBinding {
        return FragmentEditPostBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(EDIT_POST_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        editPostExtras = arguments?.getParcelable(EDIT_POST_EXTRAS)!!
    }

    override fun setUpViews() {
        super.setUpViews()

        setBindingVariables()
        fetchUserFromDB()
        initMemberTaggingView()
        initToolbar()
        fetchPost()
        initPostSaveListener()
    }

    // sets the binding variables
    private fun setBindingVariables() {
        binding.toolbarColor = LMBranding.getToolbarColor()
        binding.buttonColor = LMBranding.getButtonsColor()
    }


    // fetches user data from local db
    private fun fetchUserFromDB() {
        helperViewModel.fetchUserFromDB()
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

    // initializes the toolbar
    private fun initToolbar() {
        binding.apply {
            toolbarColor = LMBranding.getToolbarColor()

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

            ivBack.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    // fetches the post data
    private fun fetchPost() {
        ProgressHelper.showProgress(binding.progressBar)
        viewModel.getPost(editPostExtras.postId)
    }

    // initializes post done button click listener
    private fun initPostSaveListener() {
        binding.apply {
            tvSave.setOnClickListener {
                val text = etPostContent.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                handleSaveButton(clickable = true, showProgress = true)
                viewModel.editPost(
                    editPostExtras.postId,
                    updatedText,
                    attachments = fileAttachments,
                    ogTags = ogTags
                )
            }
        }
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

            if (fileAttachments == null) {
                // text watcher with debounce to add delay in api calls for ogTags
                textChanges()
                    .debounce(500)
                    .distinctUntilChanged()
                    .onEach {
                        val text = it?.toString()?.trim()
                        if (!text.isNullOrEmpty()) {
                            showLinkPreview(text)
                        }
                    }
                    .launchIn(lifecycleScope)
            }

            // text watcher to handleSaveButton click-ability
            addTextChangedListener {
                val text = it?.toString()?.trim()
                if (text.isNullOrEmpty()) {
                    clearPreviewLink()
                    if (fileAttachments != null) {
                        handleSaveButton(clickable = true)
                    } else {
                        handleSaveButton(clickable = false)
                    }
                } else {
                    handleSaveButton(clickable = true)
                }
            }
        }
    }

    // handles Save Done button click-ability
    private fun handleSaveButton(
        clickable: Boolean,
        showProgress: Boolean? = null
    ) {
        binding.apply {
            if (showProgress == true) {
                pbSaving.show()
                tvSave.hide()
            } else {
                pbSaving.hide()
                if (clickable) {
                    tvSave.isClickable = true
                    tvSave.setTextColor(LMBranding.getButtonsColor())
                } else {
                    tvSave.isClickable = false
                    tvSave.setTextColor(Color.parseColor("#666666"))
                }
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

    // clears link preview
    private fun clearPreviewLink() {
        ogTags = null
        binding.linkPreview.apply {
            root.hide()
        }
    }

    override fun observeData() {
        super.observeData()

        // observes error message
        observeErrors()
        observeMembersTaggingList()

        // observes userData and initializes the user view
        helperViewModel.userData.observe(viewLifecycleOwner) {
            initAuthorFrame(it)
        }

        // observes postResponse live data
        viewModel.postDataEventFlow.onEach { response ->
            when (response) {
                is EditPostViewModel.PostDataEvent.EditPost -> {
                    // updated post from editPost response

                    val post = response.post

                    // notifies the subscribers about the change in post data
                    postEvent.notify(Pair(post.id, post))

                    requireActivity().apply {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
                is EditPostViewModel.PostDataEvent.GetPost -> {
                    // post from getPost response

                    val post = response.post
                    setPostData(post)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

        // observes decodeUrlResponse and returns link ogTags
        helperViewModel.decodeUrlResponse.observe(viewLifecycleOwner) { ogTags ->
            this.ogTags = ogTags
            initLinkView()
        }
    }

    // observes error events
    private fun observeErrors() {
        viewModel.errorEventFlow.onEach { response ->
            when (response) {
                is EditPostViewModel.ErrorMessageEvent.EditPost -> {
                    handleSaveButton(clickable = true, showProgress = false)
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }
                is EditPostViewModel.ErrorMessageEvent.GetPost -> {
                    requireActivity().apply {
                        ViewUtils.showErrorMessageToast(this, response.errorMessage)
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
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
     * Observes for member tagging list, This is a live observer which will update itself on addition of new members
     * [taggingData] contains first -> page called in api
     * second -> Community Members and Groups
     */
    private fun observeMembersTaggingList() {
        helperViewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    // sets the post data in view
    private fun setPostData(post: PostViewData) {
        binding.apply {
            val attachments = post.attachments
            ProgressHelper.hideProgress(progressBar)
            nestedScroll.show()

            // decodes the post text and sets to the edit text
            MemberTaggingDecoder.decode(
                etPostContent,
                post.text,
                LMBranding.getTextLinkColor()
            )

            // sets the cursor to the end and opens keyboard
            etPostContent.setSelection(etPostContent.length())
            etPostContent.focusAndShowKeyboard()

            when (post.viewType) {
                ITEM_POST_SINGLE_IMAGE -> {
                    fileAttachments = attachments
                    showSingleImagePreview()
                }
                ITEM_POST_SINGLE_VIDEO -> {
                    fileAttachments = attachments
                    showSingleVideoPreview()
                }
                ITEM_POST_DOCUMENTS -> {
                    fileAttachments = attachments
                    showDocumentsPreview()
                }
                ITEM_POST_MULTIPLE_MEDIA -> {
                    fileAttachments = attachments
                    showMultimediaPreview()
                }
                ITEM_POST_LINK -> {
                    ogTags = attachments.first().attachmentMeta.ogTags
                    initLinkView()
                }
                else -> {
                }
            }
            initPostContentTextListener()
        }
    }

    // shows single image preview
    private fun showSingleImagePreview() {
        handleSaveButton(clickable = true)
        val attachmentUrl = fileAttachments?.first()?.attachmentMeta?.url ?: return
        // gets the shimmer drawable for placeholder
        val shimmerDrawable = ViewUtils.getShimmer()
        binding.apply {
            singleImageAttachment.root.show()
            ImageBindingUtil.loadImage(
                singleImageAttachment.ivSingleImagePost,
                attachmentUrl,
                placeholder = shimmerDrawable
            )
        }
    }

    // shows single video preview
    private fun showSingleVideoPreview() {
        // TODO: exo player
    }

    // shows documents preview
    private fun showDocumentsPreview() {
        binding.apply {
            handleSaveButton(clickable = true)
            documentsAttachment.root.show()
            val mDocumentsAdapter = EditPostDocumentsAdapter()
            // item decorator to add spacing between items
            val dividerItemDecorator =
                DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL)
            dividerItemDecorator.setDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.document_item_divider
                ) ?: return
            )
            documentsAttachment.rvDocuments.apply {
                adapter = mDocumentsAdapter
                layoutManager = LinearLayoutManager(root.context)
                // if separator is not there already, then only add
                if (itemDecorationCount < 1) {
                    addItemDecoration(dividerItemDecorator)
                }
            }

            val documents = fileAttachments ?: return

            if (documents.size <= PostTypeUtil.SHOW_MORE_COUNT) {
                documentsAttachment.tvShowMore.hide()
                mDocumentsAdapter.replace(documents)
            } else {
                documentsAttachment.tvShowMore.show()
                "+${documents.size - PostTypeUtil.SHOW_MORE_COUNT} more".also {
                    documentsAttachment.tvShowMore.text = it
                }
                mDocumentsAdapter.replace(documents.take(PostTypeUtil.SHOW_MORE_COUNT))
            }

            documentsAttachment.tvShowMore.setOnClickListener {
                documentsAttachment.tvShowMore.hide()
                mDocumentsAdapter.replace(documents)
            }
        }
    }

    // shows multimedia preview
    private fun showMultimediaPreview() {
        handleSaveButton(clickable = true)
        binding.apply {
            multipleMediaAttachment.root.show()
            multipleMediaAttachment.buttonColor = LMBranding.getButtonsColor()
            val multiMediaAdapter = MultipleMediaPostAdapter()
            multipleMediaAttachment.viewpagerMultipleMedia.adapter = multiMediaAdapter
            multipleMediaAttachment.dotsIndicator.setViewPager2(multipleMediaAttachment.viewpagerMultipleMedia)
            val attachments = fileAttachments?.map {
                when (it.attachmentType) {
                    IMAGE -> {
                        it.toBuilder().dynamicViewType(ITEM_MULTIPLE_MEDIA_IMAGE).build()
                    }
                    VIDEO -> {
                        it.toBuilder().dynamicViewType(ITEM_MULTIPLE_MEDIA_VIDEO).build()
                    }
                    else -> {
                        it
                    }
                }
            } ?: return
            multiMediaAdapter.replace(attachments)
        }
    }

    // renders data in the link view
    private fun initLinkView() {
        val data = ogTags ?: return
        val link = data.url ?: ""
        // sends link attached event with the link
        helperViewModel.sendLinkAttachedEvent(link)
        binding.linkPreview.apply {
            root.show()

            val isImageValid = data.image.isImageValid()
            ivLink.isVisible = isImageValid
            LinkUtil.handleLinkPreviewConstraints(this, isImageValid)

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
}