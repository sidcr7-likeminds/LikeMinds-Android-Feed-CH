package com.likeminds.feedsx.db.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.likeminds.feedsx.db.utils.DbConstants.LINK_OG_TAGS_TABLE

@Entity(tableName = LINK_OG_TAGS_TABLE)
class LinkOGTagsEntity constructor(
    @ColumnInfo(name = "og_tags_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "image")
    val image: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "og_tags_url")
    val url: String
) {
    class Builder {
        private var id: Long = 0
        private var title: String = ""
        private var image: String = ""
        private var description: String = ""
        private var url: String = ""

        fun id(id: Long) = apply { this.id = id }
        fun title(title: String) = apply { this.title = title }
        fun image(image: String) = apply { this.image = image }
        fun description(description: String) = apply { this.description = description }
        fun url(url: String) = apply { this.url = url }

        fun build() =
            LinkOGTagsEntity(
                id,
                title,
                image,
                description,
                url
            )
    }

    fun toBuilder(): Builder {
        return Builder().id(id)
            .title(title)
            .image(image)
            .description(description)
            .url(url)
    }
}