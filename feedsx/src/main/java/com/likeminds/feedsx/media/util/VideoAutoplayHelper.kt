package com.likeminds.feedsx.media.util

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsx.databinding.ItemMultipleMediaVideoBinding
import com.likeminds.feedsx.databinding.ItemPostMultipleMediaBinding
import com.likeminds.feedsx.databinding.ItemPostSingleVideoBinding
import com.likeminds.feedsx.media.customviews.LikeMindsVideoPlayerView
import com.likeminds.feedsx.post.detail.view.adapter.PostDetailAdapter
import com.likeminds.feedsx.posttypes.model.PostViewData
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter
import com.likeminds.feedsx.utils.customview.DataBoundViewHolder
import com.likeminds.feedsx.utils.model.ITEM_POST_MULTIPLE_MEDIA
import com.likeminds.feedsx.utils.model.ITEM_POST_SINGLE_VIDEO
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.min

@Singleton
class VideoAutoPlayHelper private constructor(private val recyclerView: RecyclerView) {
    companion object {
        private var videoAutoPlayHelper: VideoAutoPlayHelper? = null

        fun getInstance(recyclerView: RecyclerView): VideoAutoPlayHelper {
            if (videoAutoPlayHelper == null) {
                videoAutoPlayHelper = VideoAutoPlayHelper(recyclerView)
            }
            return videoAutoPlayHelper!!
        }

        fun getInstance(): VideoAutoPlayHelper? {
            return videoAutoPlayHelper
        }

        fun destroy() {
            videoAutoPlayHelper = null
        }
    }

    private var lastPlayerView: LikeMindsVideoPlayerView? = null

    // When playerView will be less than [MIN_LIMIT_VISIBILITY]% visible than it will stop the player
    private val MIN_LIMIT_VISIBILITY = 20

    private var currentPlayingVideoItemPos = -1 // -1 indicates nothing playing

    private fun attachVideoPlayerAt(pos: Int) {
        recyclerView.adapter.apply {
            when (this) {
                is PostAdapter -> {
                    val item = this[pos]
                    handleVideoPlay(
                        pos,
                        (item?.viewType ?: 0),
                        item as PostViewData
                    )
                }
                is PostDetailAdapter -> {
                    Log.d("PUI", "attachVideoPlayerAt: $pos")
                    if (pos != 0) {
                        return
                    }
                    val item = this[pos]
                    handleVideoPlay(
                        pos,
                        (item?.viewType ?: 0),
                        item as PostViewData
                    )
                }
            }
        }
    }

    private fun handleVideoPlay(
        pos: Int,
        viewType: Int,
        data: PostViewData
    ) {
        when (viewType) {
            ITEM_POST_SINGLE_VIDEO -> {
                val itemPostSingleVideoBinding =
                    (recyclerView.findViewHolderForAdapterPosition(pos) as? DataBoundViewHolder<*>)
                        ?.binding as? ItemPostSingleVideoBinding ?: return

                /** in case its only video **/
                if (lastPlayerView == null || lastPlayerView != itemPostSingleVideoBinding.videoPost) {
                    val videoUri = Uri.parse(data.attachments.first().attachmentMeta.url)
                    itemPostSingleVideoBinding.videoPost.startPlaying(videoUri)
                    // stop last player
                    lastPlayerView?.removePlayer()
                }
                lastPlayerView = itemPostSingleVideoBinding.videoPost
            }
            ITEM_POST_MULTIPLE_MEDIA -> {
                val itemMultipleMediaBinding =
                    (recyclerView.findViewHolderForAdapterPosition(pos) as? DataBoundViewHolder<*>)
                        ?.binding as? ItemPostMultipleMediaBinding ?: return

                val viewPager = itemMultipleMediaBinding.viewpagerMultipleMedia

                val itemMultipleMediaVideoBinding =
                    ((viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(viewPager.currentItem) as? DataBoundViewHolder<*>)
                        ?.binding as? ItemMultipleMediaVideoBinding

                lastPlayerView = if (itemMultipleMediaVideoBinding == null) {
                    // stop last player
                    lastPlayerView?.removePlayer()
                    null
                } else {
                    /** in case its only video **/
                    if (lastPlayerView == null || lastPlayerView != itemMultipleMediaVideoBinding.videoPost) {
                        val videoUri =
                            Uri.parse(data.attachments[viewPager.currentItem].attachmentMeta.url)
                        itemMultipleMediaVideoBinding.videoPost.startPlaying(videoUri)
                        // stop last player
                        lastPlayerView?.removePlayer()
                    }
                    itemMultipleMediaVideoBinding.videoPost
                }
            }
            else -> {
                /** in case its a image **/
                if (lastPlayerView != null) {
                    // stop last player
                    lastPlayerView?.removePlayer()
                    lastPlayerView = null
                }
            }
        }
    }

    private fun getMostVisibleItem() {
        val firstVisiblePosition: Int = findFirstVisibleItemPosition()
        val lastVisiblePosition: Int = findLastVisibleItemPosition()

        var maxPercentage = -1
        var pos = 0
        recyclerView.post {
            for (i in firstVisiblePosition..lastVisiblePosition) {
                val viewHolder: RecyclerView.ViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(i) ?: return@post

                val currentPercentage = getVisiblePercentage(viewHolder)
                if (currentPercentage > maxPercentage) {
                    maxPercentage = currentPercentage.toInt()
                    pos = i
                }
            }
            if (maxPercentage == -1 || maxPercentage < MIN_LIMIT_VISIBILITY) {
                pos = -1
            }
            if (pos == -1) {
                /*check if current view is more than MIN_LIMIT_VISIBILITY*/
                if (currentPlayingVideoItemPos != -1) {
                    val viewHolder: RecyclerView.ViewHolder =
                        recyclerView.findViewHolderForAdapterPosition(currentPlayingVideoItemPos)!!

                    val currentVisibility = getVisiblePercentage(viewHolder)
                    if (currentVisibility < MIN_LIMIT_VISIBILITY) {
                        lastPlayerView?.removePlayer()
                    }
                    currentPlayingVideoItemPos = -1
                }
            } else {
//                if (currentPlayingVideoItemPos != pos) {
                currentPlayingVideoItemPos = pos
                attachVideoPlayerAt(pos)
//                }
            }
        }
    }

    private fun getVisiblePercentage(
        holder: RecyclerView.ViewHolder
    ): Float {
        val rectParent = Rect()
        recyclerView.getGlobalVisibleRect(rectParent)
        val location = IntArray(2)
        holder.itemView.getLocationOnScreen(location)

        val rectChild = Rect(
            location[0],
            location[1],
            location[0] + holder.itemView.width,
            location[1] + holder.itemView.height
        )

        val rectParentArea =
            ((rectChild.right - rectChild.left) * (rectChild.bottom - rectChild.top)).toFloat()
        val xOverlap = max(
            0,
            min(
                rectChild.right,
                rectParent.right
            ) - max(
                rectChild.left,
                rectParent.left
            )
        ).toFloat()

        val yOverlap = max(
            0,
            min(
                rectChild.bottom,
                rectParent.bottom
            ) - max(
                rectChild.top,
                rectParent.top
            )
        ).toFloat()

        val overlapArea = xOverlap * yOverlap
        return (overlapArea / rectParentArea * 100.0f)
    }


    private fun findFirstVisibleItemPosition(): Int {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            return (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        }
        return -1
    }

    private fun findLastVisibleItemPosition(): Int {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            return (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        return -1
    }

    fun startObserving() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when (recyclerView.adapter) {
                    is PostAdapter -> {
                        getMostVisibleItem()
                    }

                    is PostDetailAdapter -> {
                        getVisibilityOfPost()
                    }
                }
            }
        })
    }

    private fun getVisibilityOfPost() {
        recyclerView.post {
            val viewHolder: RecyclerView.ViewHolder =
                recyclerView.findViewHolderForAdapterPosition(0) ?: return@post

            if (getVisiblePercentage(viewHolder) > MIN_LIMIT_VISIBILITY) {
                attachVideoPlayerAt(0)
            } else {
                if (lastPlayerView != null) {
                    // stop last player
                    lastPlayerView?.removePlayer()
                    lastPlayerView = null
                }
            }
        }
    }

    fun logic() {
        getMostVisibleItem()
    }

    fun logic1() {
        getVisibilityOfPost()
    }

    fun removePlayer() {
        if (lastPlayerView != null) {
            // stop last player
            lastPlayerView?.removePlayer()
            lastPlayerView = null
        }
    }
}