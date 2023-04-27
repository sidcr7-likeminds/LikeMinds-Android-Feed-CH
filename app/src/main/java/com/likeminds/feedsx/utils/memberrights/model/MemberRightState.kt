package com.likeminds.feedsx.utils.memberrights.model

import androidx.annotation.IntDef

const val MEMBER_RIGHT_CREATE_POSTS = 9
const val MEMBER_RIGHT_COMMENT_AND_REPLY_ON_POSTS = 10

@IntDef(
    MEMBER_RIGHT_CREATE_POSTS,
    MEMBER_RIGHT_COMMENT_AND_REPLY_ON_POSTS
)
@Retention(AnnotationRetention.SOURCE)
annotation class MemberRightsState