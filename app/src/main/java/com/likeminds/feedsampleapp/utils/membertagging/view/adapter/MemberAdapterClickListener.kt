package com.likeminds.feedsampleapp.utils.membertagging.view.adapter

import com.likeminds.feedsampleapp.utils.membertagging.model.UserTagViewData

internal interface MemberAdapterClickListener {

    fun onMemberTagged(user: UserTagViewData)

}