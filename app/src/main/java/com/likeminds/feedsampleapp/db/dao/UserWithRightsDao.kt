package com.likeminds.feedsampleapp.db.dao

import androidx.room.*
import com.likeminds.feedsampleapp.db.models.MemberRightsEntity
import com.likeminds.feedsampleapp.db.models.UserEntity
import com.likeminds.feedsampleapp.db.models.UserWithRights
import com.likeminds.feedsampleapp.db.utils.DbConstants

@Dao
interface UserWithRightsDao {

    //add user in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    //inserts user along with rights in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWithRights(user: UserEntity, memberRights: List<MemberRightsEntity>)

    //delete user in local db
    @Delete
    suspend fun deleteUser(user: UserEntity)

    //get user for a particular user.id
    @Query("SELECT * FROM ${DbConstants.USER_TABLE} WHERE user_unique_id = :id")
    suspend fun getUser(id: String): UserEntity

    //get user for a particular user.id with rights
    @Transaction
    @Query("SELECT * FROM ${DbConstants.USER_TABLE} WHERE user_unique_id = :id")
    suspend fun getUserWithRights(id: String): UserWithRights
}