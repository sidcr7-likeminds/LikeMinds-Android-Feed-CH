package com.likeminds.feedsx.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.likeminds.feedsx.db.dao.PostWithAttachmentsDao
import com.likeminds.feedsx.db.dao.UserWithRightsDao
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.db.models.MemberRightsEntity
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.models.UserEntity

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