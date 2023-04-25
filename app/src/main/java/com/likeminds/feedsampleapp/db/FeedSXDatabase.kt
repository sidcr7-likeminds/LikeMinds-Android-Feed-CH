package com.likeminds.feedsampleapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.likeminds.feedsampleapp.db.dao.PostWithAttachmentsDao
import com.likeminds.feedsampleapp.db.dao.UserWithRightsDao
import com.likeminds.feedsampleapp.db.models.AttachmentEntity
import com.likeminds.feedsampleapp.db.models.MemberRightsEntity
import com.likeminds.feedsampleapp.db.models.PostEntity
import com.likeminds.feedsampleapp.db.models.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        AttachmentEntity::class,
        MemberRightsEntity::class
    ], version = 2, exportSchema = false
)
abstract class FeedSXDatabase : RoomDatabase() {
    //User related queries
    abstract fun userDao(): UserWithRightsDao

    //Post related queries
    abstract fun postDao(): PostWithAttachmentsDao
}