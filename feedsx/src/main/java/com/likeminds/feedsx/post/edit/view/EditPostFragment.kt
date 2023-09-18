package com.likeminds.feedsx.post.edit.view

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.*
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedFragmentEditPostBinding
import com.likeminds.feedsx.feed.util.PostEvent
import com.likeminds.feedsx.post.create.view.LMFeedCreatePostFragment
import com.likeminds.feedsx.post.create.view.LMFeedDiscardResourceDialog
import com.likeminds.feedsx.post.edit.model.EditPostExtras
import com.likeminds.feedsx.post.edit.view.EditPostActivity.Companion.EDIT_POST_EXTRAS
import com.likeminds.feedsx.post.edit.viewmodel.EditPostViewModel
import com.likeminds.feedsx.post.edit.viewmodel.HelperViewModel
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.*
import com.likeminds.feedsx.utils.membertagging.view.LMFeedMemberTaggingView
import com.likeminds.feedsx.utils.model.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class EditPostFragment :
    BaseFragment<LmFeedFragmentEditPostBinding, EditPostViewModel>(),
    LMFeedDiscardResourceDialog.DiscardResourceDialogListener,
    PostAdapterListener {

    @Inject
    lateinit var helperViewModel: HelperViewModel

    private lateinit var editPostExtras: EditPostExtras

    private var fileAttachments: List<AttachmentViewData>? = null
    private var ogTags: LinkOGTagsViewData? = null

    private lateinit var memberTagging: LMFeedMemberTaggingView

    // [postPublisher] to publish changes in the post
    private val postEvent = PostEvent.getPublisher()

    private var post: PostViewData? = null

    private var discardResourceDialog: LMFeedDiscardResourceDialog? = null

    companion object {
        const val TAG = "EditPostFragment"
    }

    override val useSharedViewModel: Boolean
        get() = true

    override fun getViewModelClass(): Class<EditPostViewModel> {
        return EditPostViewModel::class.java
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().editPostComponent()?.inject(this)
    }

    override fun getViewBinding(): LmFeedFragmentEditPostBinding {
        return LmFeedFragmentEditPostBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(EDIT_POST_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        editPostExtras = ExtrasUtil.getParcelable(
            arguments,
            EDIT_POST_EXTRAS,
            EditPostExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun setUpViews() {
        super.setUpViews()
        initView()
        fetchUserFromDB()
        initMemberTaggingView()
        initToolbar()
        fetchPost()
        initPostSaveListener()
    }

    // initializes the view
    private fun initView() {
        binding.apply {
            toolbarColor = LMFeedBranding.getToolbarColor()
            buttonColor = LMFeedBranding.getButtonsColor()

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
        }
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
                    LMFeedBranding.getTextLinkColor()
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
            toolbarColor = LMFeedBranding.getToolbarColor()

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

            ivBack.setOnClickListener {
                openBackPressedPopup()
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
            btnSave.setOnClickListener {
                val text = etPostContent.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                handleSaveButton(visible = true)
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

            // text watcher to handleSaveButton click-ability
            addTextChangedListener {
                val text = it?.toString()?.trim()
                if (text.isNullOrEmpty()) {
                } else {
                }
            }
        }
    }

    // handles Save Done button click-ability
    private fun handleSaveButton(
        visible: Boolean
    ) {
        binding.apply {
            if (visible) {
                btnSave.show()
            } else {
                btnSave.hide()
            }
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
                    post = response.post
                    initEditTextListener()
                    setPostData(response.post)
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)
    }

    // initializes a listener to edit text
    private fun initEditTextListener() {
        binding.apply {
            etPostTitle.doAfterTextChanged {
                showPostMedia(
                    etPostTitle.text?.trim().toString(),
                    etPostContent.text?.trim().toString()
                )
            }

            if (post?.viewType == ITEM_POST_ARTICLE) {
                etPostContent.doAfterTextChanged {
                    showPostMedia(
                        etPostTitle.text?.trim().toString(),
                        etPostContent.text?.trim().toString()
                    )
                }
            }
        }
    }

    // observes error events
    private fun observeErrors() {
        viewModel.errorEventFlow.onEach { response ->
            when (response) {
                is EditPostViewModel.ErrorMessageEvent.EditPost -> {
                    handleSaveButton(visible = true)
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
                is HelperViewModel.ErrorMessageEvent.GetTaggingList -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                else -> {}
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
            ProgressHelper.hideProgress(progressBar)
            nestedScroll.show()

            // decodes the post text and sets to the edit text
            MemberTaggingDecoder.decode(
                etPostContent,
                post.text,
                LMFeedBranding.getTextLinkColor()
            )

            val title = if (post.viewType == ITEM_POST_ARTICLE) {
                post.widget.widgetMetaData?.title
            } else {
                post.heading
            } ?: ""

            val content = if (post.viewType == ITEM_POST_ARTICLE) {
                post.widget.widgetMetaData?.body
            } else {
                post.text
            } ?: ""

            showPostMedia(title, content)
            // sets the cursor to the end and opens keyboard
            etPostContent.setSelection(etPostContent.length())
            etPostContent.focusAndShowKeyboard()
            initPostContentTextListener()
        }
    }

    // handles the logic to show the type of post
    private fun showPostMedia(
        title: String,
        content: String
    ) {
        val attachments = post?.attachments
        when (post?.viewType) {
            ITEM_POST_ARTICLE -> {
                showArticlePost(
                    title,
                    content
                )
            }

            ITEM_POST_SINGLE_VIDEO -> {
                fileAttachments = attachments
                showMediaPost(title)
            }

            ITEM_POST_DOCUMENTS -> {
                fileAttachments = attachments
                showMediaPost(title)
            }

            ITEM_POST_LINK -> {
                ogTags = attachments?.first()?.attachmentMeta?.ogTags
                showLinkPreview(title)
            }

            else -> {
                Log.e(SDKApplication.LOG_TAG, "invalid view type")
            }
        }
    }

    // shows add article view
    private fun showArticlePost(
        title: String,
        articleContent: String
    ) {
        binding.apply {
            cvArticleImage.show()
            // todo: edit image flow
            if (title.isEmpty() || articleContent.length < LMFeedCreatePostFragment.MIN_ARTICLE_CONTENT) {
                handleSaveButton(visible = false)
            } else {
                handleSaveButton(visible = true)
            }
            llAddArticle.hide()
            ivArticle.show()
            ivDeleteArticle.show()
            cvArticleImage.isClickable = false
            ImageBindingUtil.loadImage(
                ivArticle,
                post?.widget?.widgetMetaData?.coverImageUrl
            )
            grpMedia.hide()
            linkPreview.root.hide()
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

    // shows attached media in video/document post type
    private fun showMediaPost(
        title: String
    ) {
        binding.apply {
            ivArticle.hide()
            cvArticleImage.hide()
            linkPreview.root.hide()
            val selectedMedia = fileAttachments?.firstOrNull()
            if (selectedMedia != null) {
                if (title.isEmpty()) {
                    handleSaveButton(visible = false)
                } else {
                    handleSaveButton(visible = true)
                }
                grpMedia.show()
                tvMediaName.text = selectedMedia.attachmentMeta.name
                tvMediaSize.text =
                    getString(
                        R.string.f_MB,
                        (selectedMedia.attachmentMeta.size?.div(1000000.0))
                    )
            }
        }
    }

    // shows link preview for link post type
    private fun showLinkPreview(title: String) {
        binding.linkPreview.apply {
            if (ogTags == null) {
                return
            }
            root.show()
            ivDeleteLink.hide()

            if (title.isEmpty()) {
                handleSaveButton(visible = false)
            } else {
                handleSaveButton(visible = true)
            }
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
        }
    }

    // sets data to the author frame
    private fun initAuthorFrame(user: UserViewData) {
        binding.authorFrame.apply {
            tvCreatorName.text = user.name
            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                user.sdkClientInfoViewData.uuid,
                creatorImage,
                showRoundImage = true,
                objectKey = user.updatedAt
            )
        }
    }

    // when user clicks on discard resource
    override fun onResourceDiscarded() {
        requireActivity().finish()
    }

    // when user clicks on continue resource creation
    override fun onResourceCreationContinued() {
        discardResourceDialog?.dismiss()
    }

    // shows discard resource popup
    fun openBackPressedPopup() {
        discardResourceDialog = LMFeedDiscardResourceDialog.show(childFragmentManager)
    }
}