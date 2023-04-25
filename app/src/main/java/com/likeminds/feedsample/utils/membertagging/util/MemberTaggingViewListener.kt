package com.likeminds.feedsample.utils.membertagging.util

import com.likeminds.feedsample.utils.membertagging.model.UserTagViewData

interface MemberTaggingViewListener {

    fun onMemberTagged(user: UserTagViewData) {}

    fun onMemberRemoved(user: UserTagViewData) {}

    fun onShow() {}

    fun onHide() {}

    fun callApi(page: Int, searchName: String) {} //call tagging api
}