package com.likeminds.feedsx.db.utils

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.likeminds.feedsx.db.utils.DbConstants.ATTACHMENT_TABLE
import com.likeminds.feedsx.db.utils.DbConstants.MEMBER_RIGHTS_TABLE
import com.likeminds.feedsx.db.utils.DbConstants.USER_TABLE

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

    // migration from version-2 to version-3
    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE `$USER_TABLE` ADD COLUMN uuid TEXT DEFAULT '' NOT NULL"
            )
            database.execSQL(
                "ALTER TABLE `$USER_TABLE` ADD sdk_client_user_unique_id TEXT DEFAULT '' NOT NULL"
            )
            database.execSQL(
                "ALTER TABLE `$USER_TABLE` ADD sdk_client_uuid TEXT DEFAULT '' NOT NULL"
            )
            database.execSQL(
                "ALTER TABLE `$USER_TABLE` ADD community INTEGER DEFAULT 0 NOT NULL"
            )
            database.execSQL(
                "ALTER TABLE `$USER_TABLE` ADD user INTEGER DEFAULT 0 NOT NULL"
            )
        }
    }

    // migration from version-3 to version-4
    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE `$ATTACHMENT_TABLE` ADD COLUMN thumbnail_url TEXT"
            )
            database.execSQL(
                "ALTER TABLE `$ATTACHMENT_TABLE` ADD thumbnail_aws_folder_path TEXT"
            )
            database.execSQL(
                "ALTER TABLE `$ATTACHMENT_TABLE` ADD thumbnail_local_file_path TEXT"
            )
            database.execSQL(
                "ALTER TABLE `$ATTACHMENT_TABLE` ADD cover_image_url TEXT"
            )
            database.execSQL(
                "ALTER TABLE `$ATTACHMENT_TABLE` ADD body TEXT"
            )
            database.execSQL(
                "ALTER TABLE `$ATTACHMENT_TABLE` ADD title TEXT"
            )
        }
    }
}