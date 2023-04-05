package com.likeminds.feedsx

object LMAnalytics {
    /*
    * Event names variables
    * */
    object Events {
        const val POST_CREATION_STARTED = "Post creation started"
        const val CLICKED_ON_ATTACHMENT = "Clicked on Attachment"
        const val USER_TAGGED_IN_POST = "User tagged in a post"
        const val LINK_ATTACHED_IN_POST = "link attached in the post"
        const val IMAGE_ATTACHED_TO_POST = "Image attached to post"
        const val VIDEO_ATTACHED_TO_POST = "Video attached to post"
        const val DOCUMENT_ATTACHED_TO_POST = "Document attached in post"
        const val POST_CREATION_COMPLETED = "Post creation completed"
        const val POST_PINNED = "Post pinned"
        const val POST_UNPINNED = "Post unpinned"
        const val POST_REPORTED = "Post reported"
        const val POST_DELETED = "Post deleted"
        const val FEED_OPENED = "Feed opened"
        const val LIKE_LIST_OPEN = "Like list open"
        const val COMMENT_LIST_OPEN = "Comment list open"
        const val COMMENT_DELETED = "Comment deleted"
        const val COMMENT_REPORTED = "Comment reported"
        const val COMMENT_POSTED = "Comment posted"
        const val REPLY_POSTED = "Reply posted"
        const val REPLY_DELETED = "Reply deleted"
        const val REPLY_REPORTED = "Reply reported"
    }

    /*
    * Event keys variables
    * */
    object Keys {
        const val POST_ID = "post_id"
        const val USER_ID = "user_id"
        const val COMMENT_ID = "comment_id"
        const val COMMENT_REPLY_ID = "comment_reply_id"
        const val POST_TYPE_TEXT = "text"
        const val POST_TYPE_IMAGE = "image"
        const val POST_TYPE_VIDEO = "video"
        const val POST_TYPE_IMAGE_VIDEO = "image,video"
        const val POST_TYPE_DOCUMENT = "document"
        const val POST_TYPE_LINK = "link"
    }

    fun track(eventName: String, eventProperties: Map<String, String> = mapOf()) {

    }
}