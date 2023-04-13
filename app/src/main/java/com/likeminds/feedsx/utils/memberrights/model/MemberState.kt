package com.likeminds.feedsx.utils.memberrights.model

import androidx.annotation.IntDef

const val STATE_NOTHING = 0
const val STATE_ADMIN = 1
const val STATE_MEMBER = 4

@IntDef(
    STATE_NOTHING,
    STATE_ADMIN,
    STATE_MEMBER
)
@Retention(AnnotationRetention.SOURCE)
annotation class MemberState