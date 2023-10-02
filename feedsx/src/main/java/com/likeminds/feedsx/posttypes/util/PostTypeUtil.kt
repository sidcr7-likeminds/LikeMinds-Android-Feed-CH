package com.likeminds.feedsx.posttypes.util

import android.net.Uri
import android.text.util.Linkify
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.util.LinkifyCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.likeminds.feedsx.*
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.*
import com.likeminds.feedsx.media.util.MediaUtils
import com.likeminds.feedsx.media.util.PostVideoAutoPlayHelper
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.*
import com.likeminds.feedsx.topic.model.LMFeedTopicViewData
import com.likeminds.feedsx.utils.*
import com.likeminds.feedsx.utils.ValueUtils.getValidTextForLinkify
import com.likeminds.feedsx.utils.ValueUtils.isImageValid
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.databinding.ImageBindingUtil
import com.likeminds.feedsx.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsx.utils.membertagging.util.MemberTaggingDecoder
import com.likeminds.feedsx.utils.model.*
import java.util.Locale

object PostTypeUtil {
    const val SHOW_MORE_COUNT = 2

    // initializes author data frame on the post on home
    private fun initAuthorFrame(
        binding: LmFeedHomeAuthorFrameBinding,
        data: PostViewData,
        listener: PostAdapterListener
    ) {
        binding.apply {
            // sets button color variable in xml
            buttonColor = LMFeedBranding.getButtonsColor()

            if (data.isPinned) {
                ivPin.show()
            } else {
                ivPin.hide()
            }

            //set post title
            setTitle(tvPostTitle, data)

            root.setOnClickListener {
                listener.postDetail(data.id)
            }

            // creator data
            val user = data.user
            val postCreatorUUID = user.sdkClientInfoViewData.uuid
            tvMemberName.text = user.name

            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                postCreatorUUID,
                memberImage,
                showRoundImage = true
            )
            tvTime.text = TimeUtil.getRelativeTimeInString(data.createdAt)
        }
    }

    // initializes author data frame on the post on post detail
    private fun initAuthorFrame(
        binding: LmFeedLayoutAuthorFrameBinding,
        data: PostViewData
    ) {
        binding.apply {
            // sets button color variable in xml
            buttonColor = LMFeedBranding.getButtonsColor()
            if (data.isPinned) {
                ivPin.show()
            } else {
                ivPin.hide()
            }

            val postCreatorUUID = data.user.sdkClientInfoViewData.uuid

            // creator data
            val user = data.user
            tvMemberName.text = user.name
            if (user.customTitle.isNullOrEmpty()) {
                tvCustomTitle.hide()
            } else {
                tvCustomTitle.show()
                tvCustomTitle.text = user.customTitle
            }
            MemberImageUtil.setImage(
                user.imageUrl,
                user.name,
                postCreatorUUID,
                memberImage,
                showRoundImage = true
            )

            //sets user position
            val designation = user.listOfQuestionAnswerViewData?.firstOrNull {
                it.tag == "basic" && it.state == 1
            }?.answerOfQuestion ?: ""

            val communityName = user.listOfQuestionAnswerViewData?.firstOrNull {
                it.tag == "basic" && it.state == 0
            }?.answerOfQuestion ?: ""

            if (communityName.isEmpty()) {
                // communityName & designation, both are empty then hide the view
                if (designation.isEmpty()) {
                    tvMemberPosition.hide()
                } else {
                    // communityName is empty but designation is not empty then show designation only
                    tvMemberPosition.text = "$designation"
                }
            } else {
                if (designation.isEmpty()) {
                    // communityName is not empty but designation is empty then show @ communityName
                    tvMemberPosition.text = "@ $communityName"
                } else {
                    // show designation @ $communityName when both are non-empty
                    tvMemberPosition.text = "$designation @ $communityName"
                }
            }

            tvTime.text = TimeUtil.getRelativeTimeInString(data.createdAt)

            root.setOnClickListener {
                SDKApplication.getLMFeedUICallback()?.openProfile(
                    user.sdkClientInfoViewData.uuid,
                    user.id.toString(),
                    LMFeedAnalytics.Source.FEED
                )
            }
        }
    }

    // initializes the recyclerview with attached documents on feed
    fun initDocumentsRecyclerView(
        binding: LmFeedItemPostDocumentsBinding,
        postData: PostViewData,
        postAdapterListener: PostAdapterListener,
        position: Int
    ) {
        binding.apply {
            val context = root.context ?: return
            val mDocumentsAdapter = DocumentsPostAdapter(postAdapterListener)
            // item decorator to add spacing between items
            val dividerItemDecorator =
                DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL)
            dividerItemDecorator.setDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.document_item_divider
                ) ?: return
            )
            rvDocuments.apply {
                adapter = mDocumentsAdapter
                layoutManager = LinearLayoutManager(root.context)
                // if separator is not there already, then only add
                if (itemDecorationCount < 1) {
                    addItemDecoration(dividerItemDecorator)
                }
            }

            mDocumentsAdapter.replace(postData.attachments)
        }
    }

    // initializes the recyclerview with attached documents on feed
    fun initDocumentsRecyclerView(
        binding: LmFeedItemPostDetailDocumentsBinding,
        postData: PostViewData,
        postAdapterListener: PostAdapterListener,
        position: Int
    ) {
        binding.apply {
            val context = root.context ?: return
            val mDocumentsAdapter = DocumentsPostAdapter(postAdapterListener)
            // item decorator to add spacing between items
            val dividerItemDecorator =
                DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL)
            dividerItemDecorator.setDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.document_item_divider
                ) ?: return
            )
            rvDocuments.apply {
                adapter = mDocumentsAdapter
                layoutManager = LinearLayoutManager(root.context)
                // if separator is not there already, then only add
                if (itemDecorationCount < 1) {
                    addItemDecoration(dividerItemDecorator)
                }
            }

            val documents = postData.attachments

            if (postData.isExpanded || documents.size <= SHOW_MORE_COUNT) {
                tvShowMore.hide()
                mDocumentsAdapter.replace(postData.attachments)
            } else {
                tvShowMore.show()
                "+${documents.size - SHOW_MORE_COUNT} more".also { tvShowMore.text = it }
                mDocumentsAdapter.replace(documents.take(SHOW_MORE_COUNT))
            }

            tvShowMore.setOnClickListener {
                postAdapterListener.onMultipleDocumentsExpanded(postData, position)
            }
        }
    }

    // initializes document item of the document recyclerview
    fun initDocument(
        binding: LmFeedItemDocumentBinding,
        document: AttachmentViewData,
    ) {
        binding.apply {
            tvMeta1.hide()
            viewMetaDot1.hide()
            tvMeta2.hide()
            viewMetaDot2.hide()
            tvMeta3.hide()

            val attachmentMeta = document.attachmentMeta
            val context = root.context

            tvDocumentName.text =
                attachmentMeta.name ?: context.getString(R.string.documents)

            val noOfPage = attachmentMeta.pageCount ?: 0
            val mediaType = attachmentMeta.format
            if (noOfPage > 0) {
                tvMeta1.show()
                tvMeta1.text = context.resources.getQuantityString(
                    R.plurals.placeholder_pages,
                    noOfPage,
                    noOfPage
                )
            }
            if (attachmentMeta.size != null) {
                tvMeta2.show()
                tvMeta2.text = MediaUtils.getFileSizeText(attachmentMeta.size)
                if (tvMeta1.isVisible) {
                    viewMetaDot1.show()
                }
            }
            if (!mediaType.isNullOrEmpty() && (tvMeta1.isVisible || tvMeta2.isVisible)) {
                tvMeta3.show()
                tvMeta3.text = mediaType
                viewMetaDot2.show()
            }

            val shimmerDrawable = ViewUtils.getShimmer()
            ImageBindingUtil.loadImage(
                ivDocumentPreview,
                attachmentMeta.thumbnailUrl,
                shimmerDrawable
            )
            root.setOnClickListener {
                val pdfUri = Uri.parse(document.attachmentMeta.url ?: "")
                AndroidUtils.startDocumentViewer(root.context, pdfUri)
            }
        }
    }

    // initializes various actions on the post
    fun initActionsLayout(
        binding: LmFeedLayoutPostActionsBinding,
        data: PostViewData,
        listener: PostAdapterListener,
        position: Int
    ) {
        binding.apply {
            val context = root.context

            if (data.isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like_unfilled)
            }

            if (data.isSaved) {
                ivBookmark.setImageResource(R.drawable.ic_bookmark_filled)
            } else {
                ivBookmark.setImageResource(R.drawable.ic_bookmark_unfilled)
            }

            if (data.likesCount == 0) {
                likesCount.isEnabled = false
                likesCount.text =
                    context.getString(R.string.like)
            } else {
                likesCount.isEnabled = true
                likesCount.text =
                    context.resources.getQuantityString(
                        R.plurals.likes,
                        data.likesCount,
                        data.likesCount
                    )
                likesCount.setOnClickListener {
                    listener.showLikesScreen(data.id)
                }
            }

            commentsCount.text =
                if (data.commentsCount == 0) {
                    context.getString(R.string.add_comment)
                } else {
                    context.resources.getQuantityString(
                        R.plurals.comments,
                        data.commentsCount,
                        data.commentsCount
                    )
                }

            ivLike.setOnClickListener { view ->
                // bounce animation for like button
                ViewUtils.showBounceAnim(context, view)
                listener.likePost(position)
            }

            ivBookmark.setOnClickListener { view ->
                // bounce animation for save button
                ViewUtils.showBounceAnim(context, view)
                listener.savePost(position)
            }

            ivShare.setOnClickListener {
                listener.sharePost(data.id)
            }

            commentsCount.setOnClickListener {
                listener.comment(data.id)
            }
        }
    }

    // initializes view pager for multiple media post
    fun initViewPager(
        binding: LmFeedItemPostMultipleMediaBinding,
        data: PostViewData,
        listener: PostAdapterListener
    ) {
        binding.apply {
            // sets button color variable in xml
            buttonColor = LMFeedBranding.getButtonsColor()
            val attachments = data.attachments.map {
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
            }
            viewpagerMultipleMedia.isSaveEnabled = false
            val multipleMediaPostAdapter = MultipleMediaPostAdapter(listener)
            viewpagerMultipleMedia.adapter = multipleMediaPostAdapter

            viewpagerMultipleMedia.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    // processes the current video whenever view pager's page is changed
                    val postVideoAutoPlayHelper = PostVideoAutoPlayHelper.getInstance()
                    postVideoAutoPlayHelper?.playMostVisibleItem()
                }
            })

            dotsIndicator.setViewPager2(viewpagerMultipleMedia)
            multipleMediaPostAdapter.replace(attachments)
        }
    }

    // handles the text content of each post on post detail
    private fun initTextContent(
        tvPostTitle: TextView,
        tvPostContent: TextView,
        data: PostViewData
    ) {
        val postTitle = if (data.viewType == ITEM_POST_ARTICLE) {
            data.widget.widgetMetaData?.title
        } else {
            data.heading
        }

        if (postTitle.isNullOrEmpty()) {
            tvPostTitle.hide()
        } else {
            tvPostTitle.text = postTitle
            tvPostTitle.show()
        }

        val postContentText =
            if (data.attachments.isNotEmpty() && data.attachments.first().attachmentType == ARTICLE) {
                data.widget.widgetMetaData?.body
            } else {
                data.text
            } ?: return

        val context = tvPostContent.context

        /**
         * Text is modified as Linkify doesn't accept texts with these specific unicode characters
         * @see #Linkify.containsUnsupportedCharacters(String)
         */
        val textForLinkify = postContentText.getValidTextForLinkify()

        if (textForLinkify.isEmpty()) {
            tvPostContent.hide()
            return
        } else {
            tvPostContent.text = textForLinkify
            tvPostContent.show()
        }

        MemberTaggingDecoder.decode(
            tvPostContent,
            textForLinkify,
            enableClick = true,
            LMFeedBranding.getTextLinkColor()
        ) {
            onMemberTagClicked()
        }

        val linkifyLinks =
            (Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES or Linkify.PHONE_NUMBERS)
        LinkifyCompat.addLinks(tvPostContent, linkifyLinks)
        tvPostContent.movementMethod = CustomLinkMovementMethod { url ->
            tvPostContent.setOnClickListener {
                return@setOnClickListener
            }
            // creates a route and returns an intent to handle the link
            val intent = Route.handleDeepLink(context, url)
            if (intent != null) {
                try {
                    // starts activity with the intent
                    ActivityCompat.startActivity(context, intent, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            true
        }
    }

    // initializes single image post data
    fun initPostSingleImage(
        ivPost: ImageView,
        data: PostViewData,
        adapterListener: PostAdapterListener
    ) {
        // gets the shimmer drawable for placeholder
        val shimmerDrawable = ViewUtils.getShimmer()

        ImageBindingUtil.loadImage(
            ivPost,
            data.attachments.first().attachmentMeta.url,
            placeholder = shimmerDrawable
        )

        ivPost.setOnClickListener {
            adapterListener.postDetail(data.id)
        }
    }

    // initializes single article post data
    fun initPostArticle(
        ivPost: ImageView,
        data: PostViewData,
        adapterListener: PostAdapterListener
    ) {
        // gets the shimmer drawable for placeholder
        val shimmerDrawable = ViewUtils.getShimmer()

        ImageBindingUtil.loadImage(
            ivPost,
            data.widget.widgetMetaData?.coverImageUrl,
            placeholder = shimmerDrawable
        )

        ivPost.setOnClickListener {
            adapterListener.postDetail(data.id)
        }
    }

    // sets image in the multiple media image view
    fun initMultipleMediaImage(
        ivPost: ImageView,
        data: AttachmentViewData,
        listener: PostAdapterListener
    ) {
        ivPost.setOnClickListener {
            listener.postDetail(data.postId)
        }

        // gets the shimmer drawable for placeholder
        val shimmerDrawable = ViewUtils.getShimmer()
        ImageBindingUtil.loadImage(
            ivPost,
            data.attachmentMeta.url,
            placeholder = shimmerDrawable
        )
    }

    // handles link view in the post on feed
    fun initLinkView(
        binding: LmFeedItemPostLinkBinding,
        data: LinkOGTagsViewData
    ) {
        binding.apply {
            cvLinkPreview.setOnClickListener {
                // creates a route and returns an intent to handle the link
                val intent = Route.handleDeepLink(root.context, data.url)
                if (intent != null) {
                    try {
                        // starts activity with the intent
                        ActivityCompat.startActivity(root.context, intent, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            tvLinkTitle.text = if (data.title?.isNotBlank() == true) {
                data.title
            } else {
                root.context.getString(R.string.link)
            }

            val isImageValid = data.image.isImageValid()
            if (isImageValid) {
                ivLink.show()
                ImageBindingUtil.loadImage(
                    ivLink,
                    data.image,
                    placeholder = R.drawable.ic_link_primary_40dp,
                    cornerRadius = 8
                )
            } else {
                ivLink.hide()
            }

            tvLinkUrl.text = data.url?.lowercase(Locale.getDefault()) ?: ""
        }
    }

    // handles link view in the post detail fragment
    fun initLinkView(
        binding: LmFeedItemPostDetailLinkBinding,
        data: LinkOGTagsViewData
    ) {
        binding.apply {
            cvLinkPreview.setOnClickListener {
                // creates a route and returns an intent to handle the link
                val intent = Route.handleDeepLink(root.context, data.url)
                if (intent != null) {
                    try {
                        // starts activity with the intent
                        ActivityCompat.startActivity(root.context, intent, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            tvLinkTitle.text = if (data.title?.isNotBlank() == true) {
                data.title
            } else {
                root.context.getString(R.string.link)
            }
            tvLinkDescription.isVisible = !data.description.isNullOrEmpty()
            tvLinkDescription.text = data.description

            val isImageValid = data.image.isImageValid()
            if (isImageValid) {
                ivLink.show()
                ImageBindingUtil.loadImage(
                    ivLink,
                    data.image,
                    placeholder = R.drawable.ic_link_primary_40dp,
                    cornerRadius = 8
                )
            } else {
                ivLink.hide()
            }

            tvLinkUrl.text = data.url?.lowercase(Locale.getDefault()) ?: ""
        }
    }

    // performs action when member tag is clicked
    private fun onMemberTagClicked() {
        // member tag is clicked, open profile
    }

    // checks if binder is called from liking/saving post or not on home
    fun initPostTypeBindData(
        authorFrame: LmFeedHomeAuthorFrameBinding,
        data: PostViewData,
        position: Int,
        chipGroup: ChipGroup,
        listener: PostAdapterListener,
        returnBinder: () -> Unit,
        executeBinder: () -> Unit
    ) {
        if (data.fromPostLiked || data.fromPostSaved || data.fromVideoAction) {
            // update fromLiked/fromSaved variables and return from binder
            listener.updateFromLikedSaved(position)
            returnBinder()
        } else {
            // call all the common functions

            // sets data to the creator frame
            initAuthorFrame(
                authorFrame,
                data,
                listener
            )

            //sets topics
            initTopicsView(chipGroup, data.topics)

            executeBinder()
        }
    }

    // checks if binder is called from liking/saving post or not on post detail fragment
    fun initPostTypeBindData(
        authorFrame: LmFeedLayoutAuthorFrameBinding,
        tvPostTitle: TextView,
        tvPostContent: TextView,
        data: PostViewData,
        position: Int,
        chipGroup: ChipGroup,
        listener: PostAdapterListener,
        returnBinder: () -> Unit,
        executeBinder: () -> Unit
    ) {
        if (data.fromPostLiked || data.fromPostSaved || data.fromVideoAction) {
            // update fromLiked/fromSaved variables and return from binder
            listener.updateFromLikedSaved(position)
            returnBinder()
        } else {
            // call all the common functions

            // sets data to the creator frame
            initAuthorFrame(
                authorFrame,
                data
            )

            // sets the text content of the post
            initTextContent(
                tvPostTitle,
                tvPostContent,
                data
            )

            //sets topics
            initTopicsView(chipGroup, data.topics)
            executeBinder()
        }
    }

    private fun setTitle(
        tvPostTitle: TextView,
        data: PostViewData
    ) {
        val title = if (data.viewType == ITEM_POST_ARTICLE) {
            data.widget.widgetMetaData?.title
        } else {
            data.heading
        }

        if (title.isNullOrEmpty()) {
            tvPostTitle.hide()
        } else {
            tvPostTitle.show()
            tvPostTitle.text = title
        }
    }

    //handle topic chip group if topics are present and add individual chip for topics
    private fun initTopicsView(chipGroup: ChipGroup, topics: List<LMFeedTopicViewData>) {
        if (topics.isEmpty()) {
            chipGroup.hide()
        } else {
            chipGroup.apply {
                show()
                removeAllViews()
                topics.forEach { topic ->
                    addView(createTopicChip(this, topic.name))
                }
            }
        }
    }

    //create chip view for topic
    private fun createTopicChip(chipGroup: ChipGroup, topicName: String): Chip {
        val binding = LmFeedTopicChipBinding.inflate(
            LayoutInflater.from(chipGroup.context),
            chipGroup,
            false
        )
        binding.chipTopic.apply {
            text = topicName
            setEnsureMinTouchTargetSize(false)
        }

        return binding.chipTopic
    }
}