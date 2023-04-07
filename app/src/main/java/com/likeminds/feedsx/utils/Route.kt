package com.likeminds.feedsx.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.likeminds.feedsx.post.create.view.CreatePostActivity
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity

object Route {

    const val ROUTE_POST_DETAIL = "post_detail"
    const val ROUTE_FEED = "feed"
    const val ROUTE_CREATE_POST = "create_post"
    const val PARAM_POST_ID = "post_id"
    const val ROUTE_BROWSER = "browser"
    const val PARAM_COMMENT_ID = "comment_id"

    private const val HTTPS_SCHEME = "https"
    private const val HTTP_SCHEME = "http"
    private const val ROUTE_SCHEME = "route"

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
            ROUTE_BROWSER -> {
                intent = getRouteToBrowser(route)
            }
        }
        if (intent != null) {
            intent.flags = flags
        }
        return intent
    }

    private fun getRouteToBrowser(route: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(route.getQueryParameter("link")))
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

    fun handleDeepLink(context: Context, url: String?): Intent? {
        val data = Uri.parse(url) ?: return null
        val firstPath = createWebsiteRoute(data) ?: return null
        return getRouteIntent(
            context,
            firstPath,
            0
        )
    }

    /**
     * Community website deep link
     * https://community.likeminds.community/deutsch-in-tandem
     * @return a url with host as route, eg : route://browser
     */
    private fun createWebsiteRoute(data: Uri): String? {
        if (data.scheme == HTTPS_SCHEME || data.scheme == HTTP_SCHEME) {
            return Uri.Builder()
                .scheme(ROUTE_SCHEME)
                .authority(ROUTE_BROWSER)
                .appendQueryParameter("link", data.toString())
                .build()
                .toString()
        }
        return null
    }

    // route://create_post
    private fun getRouteToCreatePost(
        context: Context
    ): Intent {
        return CreatePostActivity.getIntent(context)
    }
}