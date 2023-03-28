package com.likeminds.feedsx.utils.membertagging.view.adapter

import com.likeminds.feedsx.utils.membertagging.model.MemberTagViewData

internal interface MemberAdapterClickListener {

    fun onMemberTagged(user: MemberTagViewData)

}