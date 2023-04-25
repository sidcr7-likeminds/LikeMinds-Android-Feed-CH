package com.likeminds.feedsample.utils

import android.content.Context
import android.content.Intent
import com.likeminds.feedsample.FeedSXApplication
import com.likeminds.feedsample.R

object ShareUtils {
    //share post with url using default sharing in Android OS
    fun sharePost(context: Context, postId: String) {
        val domain = (context.applicationContext as FeedSXApplication).getDomain()
        val shareLink = "$domain/post?post_id=$postId"
        val shareTitle = context.getString(R.string.share_post)
        shareLink(context, shareLink, shareTitle)
    }

    //create intent and open sharing options without link as text
    private fun shareLink(context: Context, shareLink: String, shareTitle: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareLink)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, shareTitle)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}