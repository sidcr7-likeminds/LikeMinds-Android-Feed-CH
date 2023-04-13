package com.likeminds.feedsx.utils.memberrights.util

import android.util.Log
import com.likeminds.feedsx.posttypes.model.UserViewData
import com.likeminds.feedsx.utils.memberrights.model.*

object MemberRightUtil {
    fun isAdmin(memberState: Int?): Boolean {
        return memberState == STATE_ADMIN
    }

    fun isNothing(memberState: Int?): Boolean {
        return memberState == STATE_NOTHING
    }

    fun isMember(memberState: Int?): Boolean {
        return memberState == STATE_MEMBER
    }

    fun hasCreatePostsRight(user: UserViewData?): Boolean {
        return when {
            user == null -> {
                false
            }
            isAdmin(user.state) -> {
                true
            }
            (isMember(user.state) && checkHasMemberRight(
                user.memberRights,
                MEMBER_RIGHT_CREATE_POSTS
            )) -> {
                true
            }
            else -> {
                false
            }
        }
    }

    fun hasCommentRight(user: UserViewData?): Boolean {
        return when {
            user == null -> false
            isAdmin(user.state) -> true
            (isMember(user.state) && checkHasMemberRight(
                user.memberRights,
                MEMBER_RIGHT_COMMENT_AND_REPLY_ON_POSTS
            )) -> return true
            else -> false
        }
    }

    private fun checkHasMemberRight(
        memberRights: List<MemberRight>,
        rightState: Int,
    ): Boolean {
        var value = false
        memberRights.singleOrNull {
            Log.d(
                "PUI", """
                right $rightState
                state ${it.state}
                title ${it.title}
                isSelected ${it.isSelected}
                isLocked ${it.isLocked}
            """.trimIndent()
            )
            it.state == rightState
        }?.let {
            value = it.isSelected && it.isLocked == false
        }
        return value
    }
}