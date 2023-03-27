package com.likeminds.feedsx.feed

import com.likeminds.feedsx.db.dao.UserDao
import com.likeminds.feedsx.db.models.UserEntity
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }

    suspend fun getUser(id: Int): UserEntity {
        return userDao.getUser(id)
    }
}