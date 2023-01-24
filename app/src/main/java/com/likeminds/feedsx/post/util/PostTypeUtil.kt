package com.likeminds.feedsx.post.util

import android.text.*
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.ItemPostMultipleMediaBinding
import com.likeminds.feedsx.databinding.LayoutAuthorFrameBinding
import com.likeminds.feedsx.databinding.LayoutPostActionsBinding
import com.likeminds.feedsx.post.adapter.MultipleMediaPostAdapter
import com.likeminds.feedsx.post.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.post.model.PostViewData
import com.likeminds.feedsx.post.view.OverflowMenuPopup
import com.likeminds.feedsx.utils.MemberImageUtil
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.getValidTextForLinkify
import com.likeminds.feedsx.utils.link.CustomLinkMovementMethod
import com.likeminds.feedsx.utils.membertagging.MemberTaggingDecoder

object PostTypeUtil {

    private const val TAG = "PostTypeUtil"

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

    fun initViewPager(binding: ItemPostMultipleMediaBinding) {
        binding.viewpagerMultipleMedia.isSaveEnabled = false
        val multipleMediaPostAdapter = MultipleMediaPostAdapter()
        binding.viewpagerMultipleMedia.adapter = multipleMediaPostAdapter
        binding.dotsIndicator.setViewPager2(binding.viewpagerMultipleMedia)
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