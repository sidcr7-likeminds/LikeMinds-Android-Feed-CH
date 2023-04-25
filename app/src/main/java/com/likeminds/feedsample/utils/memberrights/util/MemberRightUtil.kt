package com.likeminds.feedsample.utils.memberrights.util

import com.likeminds.feedsample.db.models.MemberRightsEntity
import com.likeminds.feedsample.utils.memberrights.model.MEMBER_RIGHT_COMMENT_AND_REPLY_ON_POSTS
import com.likeminds.feedsample.utils.memberrights.model.MEMBER_RIGHT_CREATE_POSTS
import com.likeminds.feedsample.utils.memberrights.model.MemberState.Companion.isAdmin
import com.likeminds.feedsample.utils.memberrights.model.MemberState.Companion.isMember

object MemberRightUtil {
    // check if user has right to post or not
    fun hasCreatePostsRight(memberState: Int, memberRights: List<MemberRightsEntity>): Boolean {
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
    fun hasCommentRight(memberState: Int, memberRights: List<MemberRightsEntity>): Boolean {
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
     * @param memberRights: list of [MemberRightsEntity] for the user
     * @param rightState: queried right
     * */
    private fun checkHasMemberRight(
        memberRights: List<MemberRightsEntity>,
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