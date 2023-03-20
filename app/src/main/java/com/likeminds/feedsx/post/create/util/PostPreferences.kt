package com.likeminds.feedsx.post.create.util

import android.app.Application
import com.likeminds.feedsx.FeedSXApplication
import com.likeminds.feedsx.utils.sharedpreferences.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostPreferences @Inject constructor(
    application: Application
) : BasePreferences(POST_PREFS, application) {

    companion object {
        const val POST_PREFS = "post_prefs"
        const val ATTACHMENT_UPLOAD_WORKER_UUID = "ATTACHMENT_UPLOAD_WORKER_UUID"

        private var postPreferences: PostPreferences? = null

        @JvmStatic
        fun getInstance(context: Application): PostPreferences {
            if (postPreferences == null) {
                postPreferences = PostPreferences(context)
            }
            return postPreferences!!
        }
    }

    fun setAttachmentUploadWorkerUUID(uuid: String) {
        putPreference(ATTACHMENT_UPLOAD_WORKER_UUID, uuid)
    }

    fun getAttachmentUploadWorkerUUID(): String {
        return getPreference(ATTACHMENT_UPLOAD_WORKER_UUID, "") ?: ""
    }
}