package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsx.db.utils.DbConstants

@Entity(tableName = DbConstants.TOPIC_TABLE)
class TopicEntity constructor(
    @ColumnInfo(name = "topic_id")
    @PrimaryKey
    var id: String,
    @ColumnInfo(name = "topic_name")
    var name: String,
    @ColumnInfo(name = "is_topic_enabled")
    var isEnabled: Boolean,
    @ColumnInfo(name = "post_id")
    val postId: String,
) {

    class Builder {
        private var id: String = ""
        private var name: String = ""
        private var isEnabled: Boolean = true
        private var postId: String = ""

        fun id(id: String) = apply { this.id = id }
        fun name(name: String) = apply { this.name = name }
        fun isEnabled(isEnabled: Boolean) = apply { this.isEnabled = isEnabled }
        fun postId(postId: String) = apply { this.postId = postId }

        fun build() = TopicEntity(id, name, isEnabled, postId)
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .isEnabled(isEnabled)
            .name(name)
            .postId(postId)
    }
}
