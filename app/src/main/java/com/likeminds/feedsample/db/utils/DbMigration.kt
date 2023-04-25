package com.likeminds.feedsample.db.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.likeminds.feedsample.db.utils.DbConstants.MEMBER_RIGHTS_TABLE

// all db migrations are written here
object DbMigration {
    // migration from version-1 to version-2
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS `${MEMBER_RIGHTS_TABLE}` (`id` INTEGER NOT NULL, `is_locked` INTEGER, `is_selected` INTEGER NOT NULL, `state` INTEGER NOT NULL, `title` TEXT NOT NULL, `subtitle` TEXT, `user_unique_id` TEXT NOT NULL, PRIMARY KEY(`id`))"
            )
        }
    }
}