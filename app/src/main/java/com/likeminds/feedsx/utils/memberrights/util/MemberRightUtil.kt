package com.likeminds.feedsx.utils.memberrights.util

import android.util.Log
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

    fun hasCreatePostsRight(memberState: Int, memberRights: List<MemberRightViewData>): Boolean {
        return when {
            isAdmin(memberState) -> {
                true
            }
            (isMember(memberState) && checkHasMemberRight(
                memberRights,
                MEMBER_RIGHT_CREATE_POSTS
            )) -> {
                true
            }
            else -> {
                false
            }
        }
    }

    fun hasCommentRight(memberState: Int, memberRights: List<MemberRightViewData>): Boolean {
        return when {
            isAdmin(memberState) -> {
                true
            }
            (isMember(memberState) && checkHasMemberRight(
                memberRights,
                MEMBER_RIGHT_COMMENT_AND_REPLY_ON_POSTS
            )) -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun checkHasMemberRight(
        memberRights: List<MemberRightViewData>,
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
            value = it.isSelected && (it.isLocked == false)
        }
        Log.d(
            "PUI", """
            value $value
            right $rightState
        """.trimIndent()
        )
        return value
    }
}