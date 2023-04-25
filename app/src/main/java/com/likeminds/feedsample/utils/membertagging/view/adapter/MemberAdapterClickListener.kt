package com.likeminds.feedsample.utils.membertagging.view.adapter

import com.likeminds.feedsample.utils.membertagging.model.UserTagViewData

internal interface MemberAdapterClickListener {

    fun onMemberTagged(user: UserTagViewData)

}