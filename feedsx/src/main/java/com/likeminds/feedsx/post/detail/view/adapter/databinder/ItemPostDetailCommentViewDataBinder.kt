package com.likeminds.feedsx.post.detail.view.adapter.databinder

import android.view.*
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.*
import com.likeminds.feedsx.databinding.LmFeedItemPostDetailCommentBinding
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.post.detail.model.ViewMoreReplyViewData
import com.likeminds.feedsx.post.detail.util.PostDetailUtil
import com.likeminds.feedsx.post.detail.view.PostDetailFragment
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter.PostDetailAdapterListener
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailReplyAdapter
import com.likeminds.feedsx.posttypes.model.CommentViewData
import com.likeminds.feedsx.utils.TimeUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ITEM_POST_DETAIL_COMMENT

@Suppress("UNCHECKED_CAST")
class ItemPostDetailCommentViewDataBinder constructor(
    private val postDetailAdapterListener: PostDetailAdapterListener,
    private val postDetailReplyAdapterListener: PostDetailReplyAdapter.PostDetailReplyAdapterListener
) : ViewDataBinder<LmFeedItemPostDetailCommentBinding, CommentViewData>() {

    private lateinit var mRepliesAdapter: PostDetailReplyAdapter
    override val viewType: Int
        get() = ITEM_POST_DETAIL_COMMENT

    override fun createBinder(parent: ViewGroup): LmFeedItemPostDetailCommentBinding {
        return LmFeedItemPostDetailCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun bindData(
        binding: LmFeedItemPostDetailCommentBinding,
        data: CommentViewData,
        position: Int
    ) {
        initCommentsView(
            binding,
            data,
            position
        )
    }

    // sets the data to comments item and handles the replies click and rv
    private fun initCommentsView(
        binding: LmFeedItemPostDetailCommentBinding,
        data: CommentViewData,
        position: Int
    ) {
        binding.apply {
            val context = root.context

            tvCommenterName.text = data.user.name

            tvCommenterName.setOnClickListener {
                SDKApplication.getLMFeedUICallback()?.openProfile(
                    data.user.sdkClientInfoViewData.uuid,
                    data.user.id.toString(),
                    LMFeedAnalytics.Source.FEED
                )
            }

            PostDetailUtil.initTextContent(
                tvCommentContent,
                data,
                position,
                postDetailAdapterListener
            )

            tvCommentTime.text = TimeUtil.getRelativeTimeInString(data.createdAt)

            if (data.isEdited) {
                viewDotEdited.show()
                tvEdited.show()
            } else {
                viewDotEdited.hide()
                tvEdited.hide()
            }

            if (data.isLiked) {
                ivLike.setImageResource(R.drawable.ic_like_comment_filled)
            } else {
                ivLike.setImageResource(R.drawable.ic_like_comment_unfilled)
            }

            if (data.likesCount == 0) {
                likesCount.isEnabled = false
                likesCount.hide()
            } else {
                likesCount.isEnabled = true
                likesCount.text =
                    context.resources.getQuantityString(
                        R.plurals.likes,
                        data.likesCount,
                        data.likesCount
                    )
                likesCount.show()

                likesCount.setOnClickListener {
                    postDetailAdapterListener.showLikesScreen(
                        data.postId,
                        data.id
                    )
                }
            }

            ivLike.setOnClickListener { view ->
                // bounce animation for like button
                ViewUtils.showBounceAnim(context, view)
                postDetailAdapterListener.likeComment(data.id)
            }

            if (data.fromCommentLiked || data.fromCommentEdited) {
                return
            } else {
                if (data.repliesCount == 0) {
                    groupReplies.hide()
                } else {
                    groupReplies.show()
                    tvReplyCount.text = context.resources.getQuantityString(
                        R.plurals.replies,
                        data.repliesCount,
                        data.repliesCount
                    )
                }

                mRepliesAdapter = PostDetailReplyAdapter(
                    postDetailAdapterListener,
                    postDetailReplyAdapterListener
                )

                rvReplies.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = mRepliesAdapter
                }

                tvReply.setOnClickListener {
                    postDetailAdapterListener.replyOnComment(
                        data.id,
                        position,
                        data.user
                    )
                }

                val commentCreatorUUID = data.user.sdkClientInfoViewData.uuid
                ivCommentMenu.setOnClickListener { view ->
                    showMenu(
                        view,
                        data.postId,
                        data.id,
                        commentCreatorUUID,
                        data.menuItems
                    )
                }

                if (data.replies.isNotEmpty()) {
                    rvReplies.show()
                    commentSeparator.hide()
                    replyCommentSeparator.show()
                    handleViewMore(data)
                } else {
                    rvReplies.hide()
                    commentSeparator.show()
                    replyCommentSeparator.hide()
                }

                tvReplyCount.setOnClickListener {
                    if (rvReplies.isVisible) {
                        // if replies view is visible then hide the view on click
                        rvReplies.hide()
                        commentSeparator.show()
                        replyCommentSeparator.hide()
                    } else {
                        // if replies view is not visible then fetch replies
                        postDetailAdapterListener.fetchReplies(
                            data.id
                        )
                    }
                }
            }
        }
    }

    //to show overflow menu for comment
    private fun showMenu(
        view: View,
        postId: String,
        commentId: String,
        commentCreatorUUID: String,
        menuItems: List<OverflowMenuItemViewData>
    ) {
        val popup = PopupMenu(view.context, view)
        menuItems.forEach { menuItem ->
            popup.menu.add(
                Menu.NONE,
                menuItem.id,
                Menu.NONE,
                menuItem.title
            )
        }

        popup.setOnMenuItemClickListener { menuItem ->
            postDetailAdapterListener.onCommentMenuItemClicked(
                postId,
                commentId,
                commentCreatorUUID,
                menuItem.itemId
            )
            true
        }
        popup.show()
    }

    // adds ViewMoreReply view when required
    private fun handleViewMore(data: CommentViewData) {
        val repliesList = data.replies.toMutableList() as MutableList<BaseViewType>
        if (repliesList.size >= data.repliesCount) {
            // if all replies are fetched then only replace the data
            mRepliesAdapter.replace(repliesList)
        } else {
            // if a subset of replies are fetched then also add [ViewMoreReplyViewData]
            val nextPage = (repliesList.size / PostDetailFragment.REPLIES_THRESHOLD) + 1
            val viewMoreReply = ViewMoreReplyViewData.Builder()
                .totalCommentsCount(data.repliesCount)
                .currentCount(data.replies.size)
                .parentCommentId(data.id)
                .page(nextPage)
                .build()
            repliesList.add(viewMoreReply)

            mRepliesAdapter.replace(repliesList)
        }
    }
}