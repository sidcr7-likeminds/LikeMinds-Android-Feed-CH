package com.likeminds.feedsx.post.edit.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.likeminds.feedsx.branding.model.LMBranding
import com.likeminds.feedsx.databinding.FragmentEditPostBinding
import com.likeminds.feedsx.post.edit.model.EditPostExtras
import com.likeminds.feedsx.post.edit.view.EditPostActivity.Companion.EDIT_POST_EXTRAS
import com.likeminds.feedsx.post.edit.viewmodel.EditPostViewModel
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.LinkOGTagsViewData
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.ProgressHelper
import com.likeminds.feedsx.utils.ValueUtils.getUrlIfExist
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.membertagging.model.MemberTaggingExtras
import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingUtil
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingViewListener
import com.likeminds.feedsx.utils.membertagging.view.MemberTaggingView
import com.likeminds.feedsx.utils.model.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class EditPostFragment : BaseFragment<FragmentEditPostBinding>() {

    private val viewModel: EditPostViewModel by viewModels()

    private lateinit var editPostExtras: EditPostExtras

    private var fileAttachments: List<AttachmentViewData>? = null
    private var ogTags: LinkOGTagsViewData? = null

    private lateinit var etPostTextChangeListener: TextWatcher

    private lateinit var memberTagging: MemberTaggingView

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

        fetchUserFromDB()
        initMemberTaggingView()
        initToolbar()
        fetchPost()
        initPostContentTextListener()
        initPostSaveListener()
    }

    // fetches user data from local db
    private fun fetchUserFromDB() {
        viewModel.fetchUserFromDB()
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
//                viewModel.sendUserTagEvent(
//                    user.userUniqueId,
//                    memberTagging.getTaggedMemberCount()
//                )
            }

            override fun callApi(page: Int, searchName: String) {
                viewModel.getMembersForTagging(page, searchName)
            }
        })
    }

    private fun initToolbar() {
        binding.apply {
            toolbarColor = LMBranding.getToolbarColor()

            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

            ivBack.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

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
//                if (!fileAttachments.isNullOrEmpty()) {
//                    handleSaveButton(clickable = true, showProgress = true)
//                    viewModel.editPost(
//                    )
//                } else {
//                    handleSaveButton(clickable = true, showProgress = true)
//                    viewModel.editPost(
//                        updatedText,
//                    )
//                }
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

            // text watcher to handlePostButton click-ability
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
                viewModel.decodeUrl(link)
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

        observeMembersTaggingList()

        // observes userData and initializes the user view
        viewModel.userData.observe(viewLifecycleOwner) {
            initAuthorFrame(it)
        }

        // observes postResponse live data
        viewModel.postResponse.observe(viewLifecycleOwner) { post ->
            setPostData(post)
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

    private fun setPostData(post: PostViewData) {
        binding.apply {
            val attachments = post.attachments
            ProgressHelper.hideProgress(progressBar)
            nestedScroll.show()

            MemberTaggingDecoder.decode(
                etPostContent,
                post.text,
                LMBranding.getTextLinkColor()
            )

            when (post.viewType) {
                ITEM_POST_SINGLE_IMAGE -> {
                    fileAttachments = attachments
                }
                ITEM_POST_SINGLE_VIDEO -> {
                    fileAttachments = attachments
                }
                ITEM_POST_DOCUMENTS -> {
                    fileAttachments = attachments
                }
                ITEM_POST_MULTIPLE_MEDIA -> {
                    fileAttachments = attachments
                }
                ITEM_POST_LINK -> {
                    ogTags = attachments.first().attachmentMeta.ogTags
                }
                else -> {
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
}