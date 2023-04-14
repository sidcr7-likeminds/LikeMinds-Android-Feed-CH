package com.likeminds.feedsx.db.dao

import androidx.room.*
import com.likeminds.feedsx.db.models.MemberRightEntity
import com.likeminds.feedsx.db.models.UserEntity
import com.likeminds.feedsx.db.models.UserWithRights
import com.likeminds.feedsx.db.utils.DbConstants

@Dao
interface UserWithRightsDao {

    //add user in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    //inserts user along with rights in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserWithRights(user: UserEntity, memberRights: List<MemberRightEntity>)

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