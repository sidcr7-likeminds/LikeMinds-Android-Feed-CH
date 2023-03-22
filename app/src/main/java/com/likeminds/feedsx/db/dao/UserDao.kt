package com.likeminds.feedsx.db.dao

import androidx.room.*
import com.likeminds.feedsx.db.models.UserEntity
import com.likeminds.feedsx.db.utils.DbConstants

@Dao
interface UserDao {

    //add user in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    //update user in local db
    @Update
    suspend fun updateUser(user: UserEntity)

    //delete user in local db
    @Delete
    suspend fun deleteUser(user: UserEntity)

    //get user for a particular user.id
    @Query("SELECT * FROM ${DbConstants.USER_TABLE} WHERE id = :id")
    suspend fun getUser(id: Int): UserEntity
}