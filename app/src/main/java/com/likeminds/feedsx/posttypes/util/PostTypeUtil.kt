package com.likeminds.feedsx.posttypes.util

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.*
import com.likeminds.feedsx.overflowmenu.view.OverflowMenuPopup
import com.likeminds.feedsx.posttypes.view.adapter.DocumentsPostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.MultipleMediaPostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.posttypes.model.AttachmentViewData
import com.likeminds.feedsx.posttypes.model.IMAGE
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.model.VIDEO
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.getValidTextForLinkify
import com.likeminds.feedsx.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsx.utils.membertagging.MemberTaggingDecoder
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_IMAGE
import com.likeminds.feedsx.utils.model.ITEM_MULTIPLE_MEDIA_VIDEO

object PostTypeUtil {

    private const val TAG = "PostTypeUtil"
    private const val SHOW_MORE_COUNT = 2

    fun initAuthorFrame(
        binding: LayoutAuthorFrameBinding,
        data: PostViewData,
        overflowMenu: OverflowMenuPopup
    ) {
        //TODO: Change pin filled drawable
        if (data.isPinned) binding.ivPin.setImageResource(R.drawable.ic_pin_filled)
        else binding.ivPin.setImageResource(R.drawable.ic_pin_unfilled)

        binding.ivPostMenu.setOnClickListener {
            showOverflowMenu(binding.ivPostMenu, overflowMenu)
        }

        // creator data
        val user = data.user
        binding.tvMemberName.text = user.name
        binding.tvCustomTitle.text = user.customTitle
        MemberImageUtil.setImage(
            user.imageUrl,
            user.name,
            data.id,
            binding.memberImage,
            showRoundImage = true
        )

        // edited post
        if (data.isEdited) {
            binding.viewDotEdited.show()
            binding.tvEdited.show()
            binding.tvTime.text = TimeUtil.getDaysHoursOrMinutes(data.updatedAt)
        } else {
            binding.viewDotEdited.hide()
            binding.tvEdited.hide()
            binding.tvTime.text = TimeUtil.getDaysHoursOrMinutes(data.createdAt)
        }
    }

    //to show the options on the post
    private fun showOverflowMenu(ivPostMenu: ImageView, overflowMenu: OverflowMenuPopup) {
        overflowMenu.showAsDropDown(
            ivPostMenu,
            -ViewUtils.dpToPx(16),
            -ivPostMenu.height / 2,
            Gravity.START
        )
    }

    fun initDocumentsRecyclerView(
        binding: ItemPostDocumentsBinding,
        postData: PostViewData,
        postAdapterListener: PostAdapterListener,
        position: Int
    ) {
        val mDocumentsAdapter = DocumentsPostAdapter(postAdapterListener)
        binding.rvDocuments.apply {
            adapter = mDocumentsAdapter
            layoutManager = LinearLayoutManager(binding.root.context)
        }

        val documents = postData.attachments

        if (postData.isExpanded || documents.size <= SHOW_MORE_COUNT) {
            binding.tvShowMore.hide()
            mDocumentsAdapter.replace(postData.attachments)
        } else {
            binding.tvShowMore.show()
            "+${documents.size - SHOW_MORE_COUNT} more".also { binding.tvShowMore.text = it }
            mDocumentsAdapter.replace(documents.take(SHOW_MORE_COUNT))
        }

        binding.tvShowMore.setOnClickListener {
            postAdapterListener.onMultipleDocumentsExpanded(postData, position)
        }
    }

    fun initDocument(
        binding: ItemDocumentBinding,
        document: AttachmentViewData,
    ) {
        binding.tvMeta1.hide()
        binding.viewMetaDot1.hide()
        binding.tvMeta2.hide()
        binding.viewMetaDot2.hide()
        binding.tvMeta3.hide()
        //TODO: set document meta once meta data is added 

//        if (attachment.meta != null) {
//            val noOfPage = attachment.meta()?.numberOfPage ?: 0
//            val size = attachment.meta()?.size ?: 0
//            val mediaType = attachment.type()
//            if (noOfPage > 0) {
//                binding.tvMeta1.show()
//                binding.tvMeta1.text = binding.root.context.getString(
//                    R.string.placeholder_pages, noOfPage
//                )
//            }
//            if (size > 0) {
//                binding.tvMeta2.show()
//                binding.tvMeta2.text = MediaUtils.getFileSizeText(size)
//                if (binding.tvMeta1.isVisible) {
//                    binding.viewMetaDot1.show()
//                }
//            }
//            if (!mediaType.isNullOrEmpty() && (binding.tvMeta1.isVisible || binding.tvMeta2.isVisible)) {
//                binding.tvMeta3.show()
//                binding.tvMeta3.text = mediaType
//                binding.viewMetaDot2.show()
//            }
//        }
    }

    fun initActionsLayout(
        binding: LayoutPostActionsBinding,
        data: PostViewData
    ) {
        //TODO: share post

        val context = binding.root.context

        if (data.isLiked) binding.ivLike.setImageResource(R.drawable.ic_liked_filled)
        else binding.ivLike.setImageResource(R.drawable.ic_liked_unfilled)

        if (data.isSaved) binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_filled)
        else binding.ivBookmark.setImageResource(R.drawable.ic_bookmark_unfilled)

        binding.likesCount.text =
            if (data.likesCount == 0) context.getString(R.string.like)
            else
                context.resources.getQuantityString(
                    R.plurals.likes,
                    data.likesCount,
                    data.likesCount
                )

        binding.commentsCount.text =
            if (data.commentsCount == 0) context.getString(R.string.add_comment)
            else
                context.resources.getQuantityString(
                    R.plurals.comments,
                    data.commentsCount,
                    data.commentsCount
                )
    }

    fun initViewPager(binding: ItemPostMultipleMediaBinding, data: PostViewData) {
        val attachments = data.attachments.map {
            when (it.fileType) {
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
        binding.viewpagerMultipleMedia.isSaveEnabled = false
        val multipleMediaPostAdapter = MultipleMediaPostAdapter()
        binding.viewpagerMultipleMedia.adapter = multipleMediaPostAdapter
        binding.dotsIndicator.setViewPager2(binding.viewpagerMultipleMedia)
        multipleMediaPostAdapter.replace(attachments)
    }

    fun initTextContent(
        tvPostContent: TextView,
        data: PostViewData,
        itemPosition: Int,
        adapterListener: PostAdapterListener? = null,
    ) {
        val textForLinkify = data.text.getValidTextForLinkify()

        var alreadySeenFullContent = data.alreadySeenFullContent == true

        if (textForLinkify.isEmpty()) {
            tvPostContent.hide()
            return
        } else {
            tvPostContent.show()
        }

        val trimmedText =
            if (!alreadySeenFullContent && !data.shortText.isNullOrEmpty()) {
                data?.shortText
            } else {
                textForLinkify
            }

        MemberTaggingDecoder.decode(
            tvPostContent,
            trimmedText,
            enableClick = true,
            BrandingData.currentAdvanced?.third ?: ContextCompat.getColor(
                tvPostContent.context,
                R.color.pure_blue
            )
        ) { tag ->
            onMemberTagClicked()
        }

        val seeMoreColor = ContextCompat.getColor(tvPostContent.context, R.color.brown_grey)
        val seeMore = SpannableStringBuilder(" See More")
        seeMore.setSpan(
            ForegroundColorSpan(seeMoreColor),
            0,
            seeMore.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val seeMoreClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                alreadySeenFullContent = true
                adapterListener?.updateSeenFullContent(itemPosition, true)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val seeMoreSpannableStringBuilder = SpannableStringBuilder()
        if (!alreadySeenFullContent && !data?.shortText.isNullOrEmpty()) {
            seeMoreSpannableStringBuilder.append("...")
            seeMoreSpannableStringBuilder.append(seeMore)
            seeMoreSpannableStringBuilder.setSpan(
                seeMoreClickableSpan,
                3,
                seeMore.length + 3,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        val seeLessColor = ContextCompat.getColor(tvPostContent.context, R.color.brown_grey)
        val seeLess = SpannableStringBuilder(" See Less")
        seeLess.setSpan(
            ForegroundColorSpan(seeLessColor),
            0,
            seeLess.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val seeLessClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                alreadySeenFullContent = false
                adapterListener?.updateSeenFullContent(itemPosition, false)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }
        val seeLessSpannableStringBuilder = SpannableStringBuilder()
        if (alreadySeenFullContent && !data?.shortText.isNullOrEmpty()) {
            seeLessSpannableStringBuilder.append(seeLess)
            seeLessSpannableStringBuilder.setSpan(
                seeLessClickableSpan,
                0,
                seeLess.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        tvPostContent.movementMethod = CustomLinkMovementMethod {
            //TODO: Handle links etc.
            true
        }

        tvPostContent.text = TextUtils.concat(
            tvPostContent.text,
            seeMoreSpannableStringBuilder,
            seeLessSpannableStringBuilder
        )
    }

    fun onMemberTagClicked() {
        // TODO: Change Implementation
    }

}