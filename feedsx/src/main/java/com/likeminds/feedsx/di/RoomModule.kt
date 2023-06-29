package com.likeminds.feedsx.di

import android.content.Context
import androidx.room.Room
import com.likeminds.feedsx.db.FeedSXDatabase
import com.likeminds.feedsx.db.dao.PostWithAttachmentsDao
import com.likeminds.feedsx.db.dao.UserWithRightsDao
import com.likeminds.feedsx.db.utils.DbConstants
import com.likeminds.feedsx.db.utils.DbMigration
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {

    @Provides
    @Singleton
    fun provideFeedSXDatabase(context: Context): FeedSXDatabase {
        return Room.databaseBuilder(context, FeedSXDatabase::class.java, DbConstants.FEED_SX_DB)
            .addMigrations(DbMigration.MIGRATION_1_2, DbMigration.MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: FeedSXDatabase): UserWithRightsDao {
        return db.userDao()
    }

    @Provides
    @Singleton
    fun providePostDao(db: FeedSXDatabase): PostWithAttachmentsDao {
        return db.postDao()
    }
}