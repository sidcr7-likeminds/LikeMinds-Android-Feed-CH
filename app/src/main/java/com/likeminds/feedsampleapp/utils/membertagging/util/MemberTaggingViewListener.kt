package com.likeminds.feedsampleapp.utils.membertagging.util

import com.likeminds.feedsampleapp.utils.membertagging.model.UserTagViewData

interface MemberTaggingViewListener {

    fun onMemberTagged(user: UserTagViewData) {}

    fun onMemberRemoved(user: UserTagViewData) {}

    fun onShow() {}

    fun onHide() {}

    fun callApi(page: Int, searchName: String) {} //call tagging api
}