package com.likeminds.feedsx.feed

import com.likeminds.feedsx.db.dao.UserWithRightsDao
import com.likeminds.feedsx.db.models.MemberRightEntity
import com.likeminds.feedsx.db.models.UserEntity
import com.likeminds.feedsx.db.models.UserWithRights
import javax.inject.Inject

class UserWithRightsRepository @Inject constructor(
    private val userWithRightsDao: UserWithRightsDao
) {
    suspend fun insertUser(user: UserEntity) {
        userWithRightsDao.insertUser(user)
    }

    suspend fun insertUserWithRights(user: UserEntity, memberRights: List<MemberRightEntity>) {
        userWithRightsDao.insertUserWithRights(user, memberRights)
    }

    suspend fun deleteUser(user: UserEntity) {
        userWithRightsDao.deleteUser(user)
    }

    suspend fun getUser(id: String): UserEntity {
        return userWithRightsDao.getUser(id)
    }

    suspend fun getUserWithRights(userUniqueId: String): UserWithRights {
        return userWithRightsDao.getUserWithRights(userUniqueId)
    }
}