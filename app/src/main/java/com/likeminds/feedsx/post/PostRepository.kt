package com.likeminds.feedsx.post

import com.likeminds.feedsx.db.dao.PostDao
import com.likeminds.feedsx.db.models.PostEntity
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postDao: PostDao
) {
    suspend fun insertPost(post: PostEntity) {
        postDao.insertPost(post)
    }

    suspend fun updatePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    suspend fun deletePost(post: PostEntity) {
        postDao.updatePost(post)
    }

    suspend fun getPost(id: Int): PostEntity {
        return postDao.getPost(id)
    }
}