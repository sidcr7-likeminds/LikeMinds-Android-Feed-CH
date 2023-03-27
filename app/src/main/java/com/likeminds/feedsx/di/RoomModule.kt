package com.likeminds.feedsx.di

import android.content.Context
import androidx.room.Room
import com.likeminds.feedsx.db.FeedSXDatabase
import com.likeminds.feedsx.db.dao.UserDao
import com.likeminds.feedsx.db.utils.DbConstants
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
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(db: FeedSXDatabase): UserDao {
        return db.userDao()
    }
}