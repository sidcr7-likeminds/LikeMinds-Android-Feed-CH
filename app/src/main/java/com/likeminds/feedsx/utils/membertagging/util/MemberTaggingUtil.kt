package com.likeminds.feedsx.utils.membertagging.util

import com.likeminds.feedsx.utils.ViewDataConverter
import com.likeminds.feedsx.utils.membertagging.model.MemberTagViewData
import com.likeminds.feedsx.utils.membertagging.view.MemberTaggingView
import com.likeminds.likemindsfeed.helper.model.TagMember

object MemberTaggingUtil {

    const val PAGE_SIZE = 20

    /**
     * @return tagging list to the view
     * */
    fun getTaggingData(
        memberTags: List<TagMember>
    ): ArrayList<MemberTagViewData> {
        //list send to view
        val listOfTaggedMembers = ArrayList<MemberTagViewData>()

        //convert member tag data list
        val memberTagViewData = ArrayList(memberTags).map { memberTag ->
            ViewDataConverter.convertMemberTag(memberTag)
        }

        listOfTaggedMembers.addAll(memberTagViewData)

        return listOfTaggedMembers
    }

    /**
     * handles result and set result to [memberTagging] view as per [page]
     * */
    fun setMembersInView(
        memberTagging: MemberTaggingView,
        result: Pair<Int, ArrayList<MemberTagViewData>>?
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
}