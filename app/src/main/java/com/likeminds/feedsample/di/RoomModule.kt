package com.likeminds.feedsample.di

import android.content.Context
import androidx.room.Room
import com.likeminds.feedsample.db.FeedSXDatabase
import com.likeminds.feedsample.db.dao.PostWithAttachmentsDao
import com.likeminds.feedsample.db.dao.UserWithRightsDao
import com.likeminds.feedsample.db.utils.DbConstants
import com.likeminds.feedsample.db.utils.DbMigration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideFeedSXDatabase(@ApplicationContext context: Context): FeedSXDatabase {
        return Room.databaseBuilder(context, FeedSXDatabase::class.java, DbConstants.FEED_SX_DB)
            .addMigrations(DbMigration.MIGRATION_1_2)
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