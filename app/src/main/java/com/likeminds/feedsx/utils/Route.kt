package com.likeminds.feedsx.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity
import com.likeminds.feedsx.post.create.view.CreatePostActivity

object Route {

    const val ROUTE_POST_DETAIL = "post_detail"
    const val ROUTE_FEED = "feed"
    const val ROUTE_CREATE_POST = "create_post"

    const val PARAM_POST_ID = "post_id"
    const val PARAM_COMMENT_ID = "comment_id"

    fun getRouteIntent(
        context: Context,
        routeString: String,
        flags: Int = 0
    ): Intent? {
        val route = Uri.parse(routeString)
        var intent: Intent? = null
        when (route.host) {
            ROUTE_POST_DETAIL -> {
                intent = getRouteToPostDetail(
                    context,
                    route
                )
            }
            ROUTE_FEED -> {
                //TODO: navigation to feed fragment
            }
            ROUTE_CREATE_POST -> {
                intent = getRouteToCreatePost(context)
            }
        }
        if (intent != null) {
            intent.flags = flags
        }
        return intent
    }

    // route://post_detail?post_id=<post_id>&comment_id=<comment_id of new comment>
    private fun getRouteToPostDetail(
        context: Context,
        route: Uri
    ): Intent {
        val postId = route.getQueryParameter(PARAM_POST_ID)
        val commentId = route.getQueryParameter(PARAM_COMMENT_ID)

        val builder = PostDetailExtras.Builder()
            .postId(postId.toString())
            .isEditTextFocused(false)
            .commentId(commentId)
            .build()

        return PostDetailActivity.getIntent(
            context,
            builder
        )
    }

    //TODO: navigation to feed fragment

    // route://feed?type=
//    private fun getRouteToFeed(
//        context: Context,
//        routeString: String,
//    ): Intent {
//
//    }

    // route://create_post
    private fun getRouteToCreatePost(
        context: Context
    ): Intent {
        return CreatePostActivity.getIntent(context)
    }
}