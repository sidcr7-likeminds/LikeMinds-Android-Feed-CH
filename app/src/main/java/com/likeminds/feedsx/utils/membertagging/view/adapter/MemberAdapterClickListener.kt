package com.collabmates.membertagging.adapter

import com.collabmates.membertagging.model.MemberTagViewData

internal interface MemberAdapterClickListener {

    fun onMemberTagged(user: MemberTagViewData)

}