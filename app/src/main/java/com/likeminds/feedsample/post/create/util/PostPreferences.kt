package com.likeminds.feedsample.post.create.util

import android.content.Context
import com.likeminds.feedsample.utils.sharedpreferences.BasePreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) : BasePreferences(POST_PREFS, context) {

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