package com.likeminds.feedsx.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.likeminds.feedsx.db.dao.PostDao
import com.likeminds.feedsx.db.dao.UserDao
import com.likeminds.feedsx.db.models.AttachmentEntity
import com.likeminds.feedsx.db.models.PostEntity
import com.likeminds.feedsx.db.models.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        AttachmentEntity::class
    ], version = 1, exportSchema = false
)
abstract class FeedSXDatabase : RoomDatabase() {
    //User related queries
    abstract fun userDao(): UserDao

    //Post related queries
    abstract fun postDao(): PostDao
}