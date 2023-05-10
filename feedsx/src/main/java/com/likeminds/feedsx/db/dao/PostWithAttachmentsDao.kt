package com.likeminds.feedsx.db.dao

import androidx.room.*
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.models.PostWithAttachments
import com.likeminds.feedsx.db.utils.DbConstants

@Dao
interface PostWithAttachmentsDao {

    //add post in local db
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostWithAttachments(post: PostEntity, attachments: List<AttachmentEntity>)

    //update post in local db
    @Update
    suspend fun updatePost(post: PostEntity)

    //update post upload uuid in local db
    @Query("UPDATE ${DbConstants.POST_TABLE} SET uuid = :uuid WHERE temp_id =:temporaryId")
    suspend fun updateUploadWorkerUUID(temporaryId: Long, uuid: String)

    // updates is_posted and post_id key in db
    @Query("UPDATE ${DbConstants.POST_TABLE} SET post_id = :postId, is_posted = :isPosted WHERE temp_id =:temporaryId")
    suspend fun updateIsPosted(
        temporaryId: Long,
        postId: String,
        isPosted: Boolean
    )

    // updates post_id in attachments with temp_id
    @Query("UPDATE ${DbConstants.ATTACHMENT_TABLE} SET post_id = :postId WHERE temp_id =:temporaryId")
    suspend fun updatePostIdInAttachments(postId: String, temporaryId: Long)

    //delete post in local db
    @Delete
    suspend fun deletePost(post: PostEntity)

    //get the latest post in db which is not posted
    @Transaction
    @Query("SELECT * FROM ${DbConstants.POST_TABLE} WHERE is_posted = 0 ORDER BY temp_id DESC LIMIT ${DbConstants.LATEST_POST_LIMIT}")
    suspend fun getLatestPostWithAttachments(): PostWithAttachments?

    //get post for a particular post.id (temporaryId)
    @Transaction
    @Query("SELECT * FROM ${DbConstants.POST_TABLE} WHERE temp_id = :temporaryId")
    suspend fun getPostWithAttachments(temporaryId: Long): PostWithAttachments
}