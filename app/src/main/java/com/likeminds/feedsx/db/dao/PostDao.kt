package com.likeminds.feedsx.db.dao

import androidx.room.*
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.utils.DbConstants

@Dao
interface PostDao {

    //add post in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    //update post in local db
    @Update
    suspend fun updatePost(post: PostEntity)

    //delete post in local db
    @Delete
    suspend fun deletePost(post: PostEntity)

    //get post for a particular post.id (temporaryId)
    @Query("SELECT * FROM ${DbConstants.POST_TABLE} WHERE id = :id")
    suspend fun getPost(id: Int): PostEntity
}