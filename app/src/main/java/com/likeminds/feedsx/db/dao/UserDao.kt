package com.likeminds.feedsx.db.dao

import androidx.room.*
import com.likeminds.feedsx.db.models.UserEntity
import com.likeminds.feedsx.db.utils.DbConstants

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM ${DbConstants.USER_TABLE} WHERE id = :id")
    suspend fun getUser(id: Int): UserEntity
}