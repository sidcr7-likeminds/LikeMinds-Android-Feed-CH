package com.likeminds.feedsx.post

import com.likeminds.feedsx.db.dao.PostWithAttachmentsDao
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.models.PostWithAttachments
import com.likeminds.feedsx.db.models.TopicEntity
import javax.inject.Inject

class PostWithAttachmentsRepository @Inject constructor(
    private val postWithAttachmentsDao: PostWithAttachmentsDao
) {
    suspend fun insertPostWithAttachments(
        post: PostEntity,
        attachments: List<AttachmentEntity>,
        topics: List<TopicEntity>
    ) {
        postWithAttachmentsDao.insertPostWithAttachments(post, attachments, topics)
    }

    suspend fun updatePost(post: PostEntity) {
        postWithAttachmentsDao.updatePost(post)
    }

    suspend fun updatePostIdInAttachments(postId: String, temporaryId: Long) {
        postWithAttachmentsDao.updatePostIdInAttachments(postId, temporaryId)
    }

    suspend fun updateIsPosted(
        temporaryId: Long,
        postId: String,
        isPosted: Boolean
    ) {
        postWithAttachmentsDao.updateIsPosted(
            temporaryId,
            postId,
            isPosted
        )
    }

    suspend fun updateUploadWorkerUUID(temporaryId: Long, uuid: String) {
        postWithAttachmentsDao.updateUploadWorkerUUID(temporaryId, uuid)
    }

    suspend fun deletePost(post: PostEntity) {
        postWithAttachmentsDao.deletePost(post)
    }

    suspend fun deletePostWithTemporaryId(temporaryId: Long) {
        postWithAttachmentsDao.deletePostWithTemporaryId(temporaryId)
    }

    suspend fun getLatestPostWithAttachments(): PostWithAttachments? {
        return postWithAttachmentsDao.getLatestPostWithAttachments()
    }

    suspend fun getPostWithAttachments(temporaryId: Long): PostWithAttachments {
        return postWithAttachmentsDao.getPostWithAttachments(temporaryId)
    }
}