package com.likeminds.feedsx.post.edit.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedFragmentEditPostBinding
import com.likeminds.feedsx.feed.util.PostEvent
import com.likeminds.feedsx.media.model.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.view.LMFeedImageCropFragment
import com.likeminds.feedsx.media.view.LMFeedMediaPickerActivity
import com.likeminds.feedsx.post.create.model.RemoveDialogExtras
import com.likeminds.feedsx.post.create.view.*
import com.likeminds.feedsx.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feedsx.post.edit.view.LMFeedEditPostActivity.Companion.EDIT_POST_EXTRAS
import com.likeminds.feedsx.post.edit.viewmodel.LMFeedEditPostViewModel
import com.likeminds.feedsx.post.edit.viewmodel.LMFeedHelperViewModel
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapterListener
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionResultExtras
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.topic.util.LMFeedTopicChipUtil
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionActivity
import com.likeminds.feedsx.topic.view.LMFeedTopicSelectionAlert
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
import com.likeminds.feedsx.widgets.model.WidgetViewData
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class LMFeedEditPostFragment :
    BaseFragment<LmFeedFragmentEditPostBinding, LMFeedEditPostViewModel>(),
    LMFeedDiscardResourceDialog.DiscardResourceDialogListener,
    PostAdapterListener,
    LMFeedRemoveAttachmentDialogFragment.RemoveAttachmentDialogListener {

    @Inject
    lateinit var lmFeedHelperViewModel: LMFeedHelperViewModel

    private lateinit var editPostExtras: LMFeedEditPostExtras

    private var fileAttachments: List<AttachmentViewData>? = null
    private var widget: WidgetViewData? = null
    private var ogTags: LinkOGTagsViewData? = null

    private var articleSingleUriData: SingleUriData? = null

    private lateinit var memberTagging: LMFeedMemberTaggingView

    // [postPublisher] to publish changes in the post
    private val postEvent = PostEvent.getPublisher()

    private val workersMap by lazy { ArrayList<UUID>() }

    private var post: PostViewData? = null

    private var discardResourceDialog: LMFeedDiscardResourceDialog? = null
    private var removeAttachmentDialogFragment: LMFeedRemoveAttachmentDialogFragment? = null

    private var isEditingArticle: Boolean = false

    private val selectedTopic by lazy {
        HashMap<String, LMFeedTopicViewData>()
    }

    private val disabledTopics by lazy {
        HashMap<String, LMFeedTopicViewData>()
    }

    companion object {

        const val TAG = "EditPostFragment"
    }

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

    override fun getViewModelClass(): Class<LMFeedEditPostViewModel> {
        return LMFeedEditPostViewModel::class.java
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
            LMFeedEditPostExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun handleResultListener() {
        super.handleResultListener()

        setFragmentResultListener(LMFeedImageCropFragment.REQUEST_KEY) { _, bundle ->
            articleSingleUriData =
                ExtrasUtil.getParcelable(
                    bundle,
                    LMFeedImageCropFragment.BUNDLE_ARG_URI,
                    SingleUriData::class.java
                ) ?: return@setFragmentResultListener

            isEditingArticle = false
            showPostMedia(
                binding.etPostTitle.text?.trim().toString(),
                binding.etPostContent.text?.trim().toString()
            )
        }
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

            ivDeleteArticle.setOnClickListener {
                val removeExtras = RemoveDialogExtras.Builder()
                    .title(getString(R.string.remove_article_banner))
                    .description(getString(R.string.are_you_sure_you_want_to_remove_the_article_banner))
                    .build()
                showRemoveDialog(removeExtras)
            }

            cvArticleImage.setOnClickListener {
                initiateMediaPicker(listOf(com.likeminds.feedsx.media.model.IMAGE))
            }
        }
    }

    // triggers gallery launcher for (IMAGE)/(VIDEO)/(IMAGE & VIDEO)
    private fun initiateMediaPicker(list: List<String>) {
        val extras = MediaPickerExtras.Builder()
            .mediaTypes(list)
            .build()
        val intent = LMFeedMediaPickerActivity.getIntent(requireContext(), extras)
        galleryLauncher.launch(intent)
    }

    private fun checkMediaPickedResult(result: MediaPickerResult?) {
        if (result != null) {
            when (result.mediaPickerResultType) {
                MEDIA_RESULT_BROWSE -> {
                    val intent = AndroidUtils.getExternalPickerIntent(
                        result.mediaTypes,
                        result.allowMultipleSelect,
                        result.browseClassName
                    )
                    if (intent != null)
                        mediaBrowseLauncher.launch(intent)
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

        if (data.isNotEmpty()) {
            if (data.first().fileType == com.likeminds.feedsx.media.model.IMAGE) {
                val imageCropExtras = LMFeedImageCropExtras.Builder()
                    .singleUriData(data.first())
                    .cropWidth(16)
                    .cropHeight(9)
                    .build()

                findNavController().navigate(
                    LMFeedEditPostFragmentDirections.actionFragmentCreatePostToLmFeedImageCropFragment(
                        imageCropExtras
                    )
                )
            }
        }
    }

    private fun onMediaPickedFromGallery(data: Intent?) {
        val uris = MediaUtils.getExternalIntentPickerUris(data)
        viewModel.fetchUriDetails(requireContext(), uris) {
            val mediaUris = MediaUtils.convertMediaViewDataToSingleUriData(
                requireContext(), it
            )
            if (mediaUris.isNotEmpty()) {
                if (mediaUris.first().fileType == com.likeminds.feedsx.media.model.IMAGE) {
                    val imageCropExtras = LMFeedImageCropExtras.Builder()
                        .singleUriData(mediaUris.first())
                        .cropWidth(16)
                        .cropHeight(9)
                        .build()

                    findNavController().navigate(
                        LMFeedCreatePostFragmentDirections.actionFragmentCreatePostToLmFeedImageCropFragment(
                            imageCropExtras
                        )
                    )
                }
            }
        }
    }

    // shows media remove dialog
    private fun showRemoveDialog(removeDialogExtras: RemoveDialogExtras) {
        removeAttachmentDialogFragment = LMFeedRemoveAttachmentDialogFragment.showDialog(
            childFragmentManager,
            removeDialogExtras
        )
    }

    // fetches user data from local db
    private fun fetchUserFromDB() {
        lmFeedHelperViewModel.fetchUserFromDB()
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
                lmFeedHelperViewModel.sendUserTagEvent(
                    user.userUniqueId,
                    memberTagging.getTaggedMemberCount()
                )
            }

            override fun callApi(page: Int, searchName: String) {
                lmFeedHelperViewModel.getMembersForTagging(page, searchName)
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

            when (editPostExtras.viewType) {
                ITEM_POST_SINGLE_VIDEO -> {
                    tvToolbarTitle.text = getString(R.string.edit_video_resource)
                }

                ITEM_POST_DOCUMENTS -> {
                    tvToolbarTitle.text = getString(R.string.edit_pdf_resource)
                }

                ITEM_POST_LINK -> {
                    tvToolbarTitle.text = getString(R.string.edit_link_resource)
                }

                ITEM_POST_ARTICLE -> {
                    tvToolbarTitle.text = getString(R.string.edit_article)
                }

                else -> {
                    tvToolbarTitle.text = getString(R.string.edit_post)
                }
            }
        }
    }

    // fetches the post data
    private fun fetchPost() {
        ProgressHelper.showProgress(binding.progressBar)
        if (post == null) {
            viewModel.getPost(editPostExtras.postId)
        } else {
            val postViewData = post ?: return
            setPostData(postViewData)
        }
    }

    // initializes post done button click listener
    private fun initPostSaveListener() {
        binding.apply {
            btnSave.setOnClickListener {
                val text = etPostContent.text
                val updatedText = memberTagging.replaceSelectedMembers(text).trim()
                val title = etPostTitle.text?.trim().toString()
                val topics = selectedTopic.values

                if (selectedTopic.isNotEmpty()) {
                    if (disabledTopics.isEmpty()) {
                        savePost(title, updatedText, topics.toList())
                    } else {
                        //show dialog for disabled topics
                        showDisabledTopicsAlert(disabledTopics.values.toList())
                    }
                } else {
                    //call api as no topics are enabled
                    LMFeedTopicSelectionAlert.showDialog(childFragmentManager)
                }
            }
        }
    }

    // processes save post request and calls api
    private fun savePost(title: String, updatedText: String, topics: List<LMFeedTopicViewData>) {
        binding.apply {
            progressBar.root.show()
            handleSaveButton(false)
            if (articleSingleUriData != null) {
                viewModel.uploadArticleImage(
                    requireContext(),
                    etPostTitle.text?.trim().toString(),
                    etPostContent.text?.trim().toString(),
                    articleSingleUriData,
                    topics
                )
            } else {
                viewModel.editPost(
                    editPostExtras.postId,
                    title,
                    updatedText,
                    attachments = fileAttachments,
                    ogTags = ogTags,
                    widget = widget,
                    selectedTopics = topics
                )
            }
        }
    }

    //show alert for disabled topics
    private fun showDisabledTopicsAlert(disabledTopics: List<LMFeedTopicViewData>) {
        val noOfDisabledTopics = disabledTopics.size

        //create message string
        val topicNameString = disabledTopics.joinToString(", ") { it.name }
        val firstLineMessage = resources.getQuantityString(
            R.plurals.topic_disabled_message,
            noOfDisabledTopics,
            noOfDisabledTopics
        )
        val finalMessage = "$firstLineMessage \n $topicNameString"

        //create dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(
                resources.getQuantityString(
                    R.plurals.topic_disabled,
                    noOfDisabledTopics,
                    noOfDisabledTopics
                )
            )
            .setMessage(finalMessage)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        //set positive button color
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton?.apply {
            isAllCaps = true
            setTextColor(LMFeedBranding.getButtonsColor())
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
        }
    }

    //handles topics chip group and separator line
    private fun handleTopicSelectionView(showView: Boolean) {
        binding.apply {
            cgTopics.isVisible = showView
            topicSeparator.isVisible = showView
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
        lmFeedHelperViewModel.userData.observe(viewLifecycleOwner) {
            initAuthorFrame(it)
        }

        viewModel.uploadingData.observe(viewLifecycleOwner) { response ->
            observeUploading(response)
        }

        lmFeedHelperViewModel.showTopicFilter.observe(viewLifecycleOwner) { showTopics ->
            if (showTopics) {
                handleTopicSelectionView(true)
                if (selectedTopic.isEmpty()) {
                    initTopicSelectionView()
                }
            } else {
                handleTopicSelectionView(false)
            }
        }

        // observes postResponse live data
        viewModel.postDataEventFlow.onEach { response ->
            when (response) {
                is LMFeedEditPostViewModel.PostDataEvent.EditPost -> {
                    // updated post from editPost response

                    val post = response.post

                    // notifies the subscribers about the change in post data
                    postEvent.notify(Pair(post.id, post))

                    requireActivity().apply {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }

                is LMFeedEditPostViewModel.PostDataEvent.GetPost -> {
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
                is LMFeedEditPostViewModel.ErrorMessageEvent.EditPost -> {
                    handleSaveButton(visible = true)
                    binding.progressBar.root.hide()
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedEditPostViewModel.ErrorMessageEvent.GetPost -> {
                    requireActivity().apply {
                        ViewUtils.showErrorMessageToast(this, response.errorMessage)
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }
            }
        }.observeInLifecycle(viewLifecycleOwner)

        lmFeedHelperViewModel.errorEventFlow.onEach { response ->
            when (response) {
                is LMFeedHelperViewModel.ErrorMessageEvent.GetTaggingList -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                is LMFeedHelperViewModel.ErrorMessageEvent.GetTopic -> {
                    ViewUtils.showErrorMessageToast(requireContext(), response.errorMessage)
                }

                else -> {

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
        lmFeedHelperViewModel.taggingData.observe(viewLifecycleOwner) { result ->
            MemberTaggingUtil.setMembersInView(memberTagging, result)
        }
    }

    // sets the post data in view
    private fun setPostData(post: PostViewData) {
        binding.apply {
            val topics = post.topics.ifEmpty {
                selectedTopic.values.toList()
            }

            ProgressHelper.hideProgress(progressBar)
            nestedScroll.show()

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

            etPostTitle.setText(title)
            // decodes the post text and sets to the edit text
            MemberTaggingDecoder.decode(
                etPostContent,
                content,
                LMFeedBranding.getTextLinkColor()
            )

            showPostMedia(title, content)
            showSelectedTopic(topics)

            // sets the cursor to the end and opens keyboard
            etPostContent.setSelection(etPostContent.length())
            etPostContent.focusAndShowKeyboard()

            initPostContentTextListener()
        }
    }

    private fun showSelectedTopic(topics: List<LMFeedTopicViewData>) {
        if (topics.isNotEmpty()) {
            handleTopicSelectionView(true)

            selectedTopic.clear()
            disabledTopics.clear()

            //filter disabled topics
            topics.forEach { topic ->
                if (!topic.isEnabled) {
                    disabledTopics[topic.id] = topic
                }
            }

            addTopicsToGroup(false, topics)
        } else {
            if (lmFeedHelperViewModel.showTopicFilter.value == null) {
                lmFeedHelperViewModel.getAllTopics(true)
            }
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
                widget = post?.widget
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
            if (isEditingArticle) {
                handleSaveButton(visible = false)
                ivArticle.hide()
                ivDeleteArticle.hide()
                llAddArticle.show()
                cvArticleImage.isClickable = true
            } else {
                if (title.isEmpty() || articleContent.length < LMFeedCreatePostFragment.MIN_ARTICLE_CONTENT) {
                    handleSaveButton(visible = false)
                } else {
                    handleSaveButton(visible = true)
                }
                llAddArticle.hide()
                ivArticle.show()
                ivDeleteArticle.show()
                cvArticleImage.isClickable = false
                val imageUrl =
                    articleSingleUriData?.uri ?: post?.widget?.widgetMetaData?.coverImageUrl
                ImageBindingUtil.loadImage(
                    ivArticle,
                    imageUrl
                )
            }
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

    // observes post uploading
    private fun observeUploading(uploadingData: Triple<String, AttachmentViewData, List<LMFeedTopicViewData>>) {
        val uuid = UUID.fromString(uploadingData.first)
        if (!workersMap.contains(uuid)) {
            workersMap.add(uuid)
            WorkManager.getInstance(requireContext()).getWorkInfoByIdLiveData(uuid)
                .observe(viewLifecycleOwner) { workInfo ->
                    observeMediaWorker(workInfo, uploadingData.second, uploadingData.third)
                }
        }
    }

    // observes the media worker through various worker lifecycle
    private fun observeMediaWorker(
        workInfo: WorkInfo,
        attachmentViewData: AttachmentViewData,
        topics: List<LMFeedTopicViewData>
    ) {
        when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
                // uploading completed, call the edit post api
                val updatedWidget = widget?.toBuilder()
                    ?.metaData(
                        widget?.widgetMetaData?.toBuilder()
                            ?.url(attachmentViewData.attachmentMeta.url ?: "")
                            ?.name(attachmentViewData.attachmentMeta.name ?: "")
                            ?.coverImageUrl(attachmentViewData.attachmentMeta.coverImageUrl ?: "")
                            ?.size(attachmentViewData.attachmentMeta.size)
                            ?.build()
                    )
                    ?.build()
                viewModel.editPost(
                    editPostExtras.postId,
                    attachmentViewData.attachmentMeta.title ?: "",
                    attachmentViewData.attachmentMeta.body ?: "",
                    widget = updatedWidget,
                    selectedTopics = topics
                )
            }

            WorkInfo.State.FAILED -> {
                handleSaveButton(visible = true)
                binding.progressBar.root.hide()
                ViewUtils.showShortToast(requireContext(), getString(R.string.something_went_wrong))
            }

            else -> {}
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
        isEditingArticle = true
        showPostMedia(
            binding.etPostTitle.text?.trim().toString(),
            binding.etPostContent.text?.trim().toString()
        )
        removeAttachmentDialogFragment?.dismiss()
    }

    // when user removes attachment
    override fun onCancelled() {
        removeAttachmentDialogFragment?.dismiss()
    }

    //init initial topic selection view with "Select Topic Chip"
    private fun initTopicSelectionView() {
        binding.cgTopics.apply {
            removeAllViews()
            addView(LMFeedTopicChipUtil.createSelectTopicsChip(requireContext(), this) { intent ->
                topicSelectionLauncher.launch(intent)
            })
        }
    }

    //start activity -> Topic Selection and check for result with selected topics
    private val topicSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bundle = result.data?.extras
                val resultExtras = ExtrasUtil.getParcelable(
                    bundle,
                    LMFeedTopicSelectionActivity.TOPIC_SELECTION_RESULT_EXTRAS,
                    LMFeedTopicSelectionResultExtras::class.java
                ) ?: return@registerForActivityResult

                val selectedTopics = resultExtras.selectedTopics
                if (selectedTopics.isNotEmpty()) {
                    addTopicsToGroup(true, selectedTopics)
                }
            }
        }

    //add selected topics to group and add edit chip as well in the end
    private fun addTopicsToGroup(
        isAfterSelection: Boolean,
        newSelectedTopics: List<LMFeedTopicViewData>
    ) {
        if (isAfterSelection) {
            disabledTopics.clear()
            selectedTopic.clear()
        }

        newSelectedTopics.forEach { topic ->
            if (!topic.isEnabled) {
                disabledTopics[topic.id] = topic
            }
            selectedTopic[topic.id] = topic
        }

        val selectedTopics = selectedTopic.values.toList()

        binding.cgTopics.apply {
            removeAllViews()
            selectedTopics.forEach { topic ->
                addView(LMFeedTopicChipUtil.createTopicChip(this, topic.name))
            }
            addView(
                LMFeedTopicChipUtil.createEditChip(
                    requireContext(),
                    selectedTopics,
                    this,
                    disabledTopics.values.toList()
                ) { intent ->
                    topicSelectionLauncher.launch(intent)
                })
        }
    }
}