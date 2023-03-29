package com.likeminds.feedsx.utils.membertagging.view.adapter

import com.likeminds.feedsx.utils.membertagging.model.UserTagViewData

internal interface MemberAdapterClickListener {

    fun onMemberTagged(user: UserTagViewData)

}