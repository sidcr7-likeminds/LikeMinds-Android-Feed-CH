package com.likeminds.feedsx.post.create.util

import android.app.Application
import com.likeminds.feedsx.utils.sharedpreferences.BasePreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostPreferences @Inject constructor(
    val application: Application
) : BasePreferences(POST_PREFS, application) {

    companion object {
        const val POST_PREFS = "post_prefs"
        const val TEMPORARY_ID = "temporary_id"
    }

    fun getTemporaryId(): Long {
        return getPreference(TEMPORARY_ID, -1L)
    }

    fun saveTemporaryId(temporaryId: Long) {
        putPreference(TEMPORARY_ID, temporaryId)
    }
}