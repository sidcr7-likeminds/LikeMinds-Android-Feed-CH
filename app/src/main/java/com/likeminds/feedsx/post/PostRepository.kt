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

    suspend fun deletePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    suspend fun getLatestPostWithAttachments(): PostWithAttachments {
        return postDao.getLatestPostWithAttachments()
    }

    suspend fun getPostWithAttachments(id: Long): List<PostWithAttachments> {
        return postDao.getPostsWithAttachments(id)
    }
}