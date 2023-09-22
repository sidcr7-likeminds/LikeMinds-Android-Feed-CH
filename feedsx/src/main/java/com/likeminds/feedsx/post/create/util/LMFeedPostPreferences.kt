package com.likeminds.feedsx.post.create.util

import android.app.Application
import com.likeminds.feedsx.utils.sharedpreferences.LMFeedBasePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LMFeedPostPreferences @Inject constructor(
    application: Application
) : LMFeedBasePreferences(POST_PREFS, application) {

    companion object {
        const val POST_PREFS = "lm_feed_post_prefs"
        const val TEMPORARY_ID = "temporary_id"
    }

    fun getTemporaryId(): Long {
        return getPreference(TEMPORARY_ID, -1L)
    }

    fun saveTemporaryId(temporaryId: Long) {
        putPreference(TEMPORARY_ID, temporaryId)
    }
}