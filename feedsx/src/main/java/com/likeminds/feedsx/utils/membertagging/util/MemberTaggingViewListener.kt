package com.likeminds.feedsx.utils.membertagging.util

import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData

interface MemberTaggingViewListener {

    fun onMemberTagged(user: UserTagViewData) {
        //when a member is tagged from list
    }

    fun onMemberRemoved(user: UserTagViewData) {
        //when a tagged member is removed from the text
    }

    fun onShow() {
        //to show suggestion list
    }

    fun onHide() {
        //to hide suggestion list
    }

    fun callApi(page: Int, searchName: String) {
        //call tagging api
    }
}