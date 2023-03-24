package com.likeminds.feedsx.db.dao

import androidx.room.*
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.models.PostWithAttachments
import com.likeminds.feedsx.db.utils.DbConstants

@Dao
interface PostDao {

    //add post in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostWithAttachments(post: PostEntity, attachments: List<AttachmentEntity>)

    //update post in local db
    @Update
    suspend fun updatePost(post: PostEntity)

    //delete post in local db
    @Delete
    suspend fun deletePost(post: PostEntity)

    //get the latest post in db
    @Transaction
    @Query("SELECT * FROM ${DbConstants.POST_TABLE} ORDER BY id DESC LIMIT 1")
    suspend fun getLatestPostWithAttachments(): PostWithAttachments

    //get post for a particular post.id (temporaryId)
    @Transaction
    @Query("SELECT * FROM ${DbConstants.POST_TABLE} WHERE id = :id")
    suspend fun getPostsWithAttachments(id: Long): List<PostWithAttachments>
}