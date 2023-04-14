package com.likeminds.feedsx.utils.memberrights.util

import com.likeminds.feedsx.utils.memberrights.model.*

object MemberRightUtil {
    private fun isAdmin(memberState: Int?): Boolean {
        return memberState == STATE_ADMIN
    }

    private fun isNothing(memberState: Int?): Boolean {
        return memberState == STATE_NOTHING
    }

    private fun isMember(memberState: Int?): Boolean {
        return memberState == STATE_MEMBER
    }

    // check if user has right to post or not
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

    // check if user has right to comment or not
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

    /**
     * returns if the user has the queried right or not
     * @param memberRights: list of [MemberRightViewData] for the user
     * @param rightState: queried right
     * */
    private fun checkHasMemberRight(
        memberRights: List<MemberRightViewData>,
        rightState: Int,
    ): Boolean {
        var value = false
        memberRights.singleOrNull {
            it.state == rightState
        }?.let {
            value = it.isSelected && (it.isLocked == false)
        }
        return value
    }
}