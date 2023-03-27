package com.likeminds.feedsx.utils.membertagging.util

import com.collabmates.membertagging.model.MemberTagViewData

interface MemberTaggingViewListener {

    fun onMemberTagged(user: MemberTagViewData) {}

    fun onMemberRemoved(user: MemberTagViewData) {}

    fun onShow() {}

    fun onHide() {}

    fun callApi(page: Int, searchName: String) {} //call tagging api
}