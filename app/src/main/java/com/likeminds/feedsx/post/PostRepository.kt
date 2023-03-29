package com.likeminds.feedsx.post

import com.likeminds.feedsx.db.dao.PostDao
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.models.PostWithAttachments
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postDao: PostDao
) {
    suspend fun insertPostWithAttachments(post: PostEntity, attachments: List<AttachmentEntity>) {
        postDao.insertPostWithAttachments(post, attachments)
    }

    suspend fun updatePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    suspend fun updatePostIdInAttachments(postId: String, temporaryId: Long) {
        postDao.updatePostIdInAttachments(postId, temporaryId)
    }

    suspend fun updateIsPosted(
        temporaryId: Long,
        postId: String,
        isPosted: Boolean
    ) {
        postDao.updateIsPosted(
            temporaryId,
            postId,
            isPosted
        )
    }

    suspend fun updateUploadWorkerUUID(temporaryId: Long, uuid: String) {
        postDao.updateUploadWorkerUUID(temporaryId, uuid)
    }

    suspend fun deletePost(post: PostEntity) {
        postDao.deletePost(post)
    }

    suspend fun getLatestPostWithAttachments(): PostWithAttachments? {
        return postDao.getLatestPostWithAttachments()
    }

    suspend fun getPostWithAttachments(temporaryId: Long): PostWithAttachments {
        return postDao.getPostWithAttachments(temporaryId)
    }
}