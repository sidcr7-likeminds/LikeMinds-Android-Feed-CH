package com.likeminds.feedsampleapp.post

import com.likeminds.feedsampleapp.db.dao.PostWithAttachmentsDao
import com.likeminds.feedsampleapp.db.models.AttachmentEntity
import com.likeminds.feedsampleapp.db.models.PostEntity
import com.likeminds.feedsampleapp.db.models.PostWithAttachments
import javax.inject.Inject

class PostWithAttachmentsRepository @Inject constructor(
    private val postWithAttachmentsDao: PostWithAttachmentsDao
) {
    suspend fun insertPostWithAttachments(post: PostEntity, attachments: List<AttachmentEntity>) {
        postWithAttachmentsDao.insertPostWithAttachments(post, attachments)
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

    suspend fun getLatestPostWithAttachments(): PostWithAttachments? {
        return postWithAttachmentsDao.getLatestPostWithAttachments()
    }

    suspend fun getPostWithAttachments(temporaryId: Long): PostWithAttachments {
        return postWithAttachmentsDao.getPostWithAttachments(temporaryId)
    }
}