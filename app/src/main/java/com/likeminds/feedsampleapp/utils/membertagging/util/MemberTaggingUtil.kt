package com.likeminds.feedsampleapp.utils.membertagging.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Editable
import android.util.DisplayMetrics
import android.view.WindowInsets
import androidx.annotation.FloatRange
import com.likeminds.feedsampleapp.utils.ViewDataConverter
import com.likeminds.feedsampleapp.utils.membertagging.model.UserTagViewData
import com.likeminds.feedsampleapp.utils.membertagging.view.MemberTaggingView
import com.likeminds.likemindsfeed.helper.model.TagMember

object MemberTaggingUtil {

    const val PAGE_SIZE = 20

    /**
     * @return tagging list to the view
     * */
    fun getTaggingData(
        memberTags: List<TagMember>
    ): ArrayList<UserTagViewData> {
        //list send to view
        val listOfTaggedMembers = ArrayList<UserTagViewData>()

        //convert member tag data list
        val memberTagViewData = ArrayList(memberTags).map { memberTag ->
            ViewDataConverter.convertUserTag(memberTag)
        }

        listOfTaggedMembers.addAll(memberTagViewData)

        return listOfTaggedMembers
    }

    /**
     * handles result and set result to [memberTagging] view as per [page]
     * */
    fun setMembersInView(
        memberTagging: MemberTaggingView,
        result: Pair<Int, ArrayList<UserTagViewData>>?
    ) {
        if (result != null) {
            val page = result.first
            val list = result.second
            if (page == 1) {
                //clear and set in adapter
                memberTagging.setMembersAndGroup(list)
            } else {
                //add to the adapter
                memberTagging.addMembers(list)
            }
        } else {
            return
        }
    }

    private const val DEFAULT_MAX_HEIGHT = 300

    @JvmSynthetic
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    @JvmSynthetic
    fun getMaxHeight(
        context: Context,
        @FloatRange(from = 0.0, to = 1.0) percentage: Float
    ): Int {
        val activity = context as? Activity ?: return dpToPx(DEFAULT_MAX_HEIGHT)
        return (getDeviceHeight(activity) * percentage).toInt()
    }

    @Suppress("DEPRECATION")
    private fun getDeviceHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets =
                windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    @JvmSynthetic
    fun getLastSpan(
        editable: Editable,
        spans: Array<MemberTaggingClickableSpan>
    ): MemberTaggingClickableSpan {
        if (spans.size == 1) {
            return spans[0]
        }
        return spans.maxByOrNull {
            editable.getSpanEnd(it)
        }!!
    }

    @JvmSynthetic
    fun getSortedSpan(editable: Editable): List<MemberTaggingClickableSpan> {
        return editable.getSpans(0, editable.length, MemberTaggingClickableSpan::class.java)
            .sortedBy {
                editable.getSpanStart(it)
            }
    }
}