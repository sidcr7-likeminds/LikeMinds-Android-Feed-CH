package com.likeminds.feedsx.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.post.create.view.LMFeedCreatePostActivity
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity

object Route {

    private const val ROUTE_POST_DETAIL = "post_detail"
    private const val ROUTE_FEED = "feed"
    private const val ROUTE_CREATE_POST = "create_post"
    private const val PARAM_POST_ID = "post_id"
    private const val ROUTE_BROWSER = "browser"
    private const val PARAM_COMMENT_ID = "comment_id"

    private const val DEEP_LINK_POST = "post"

    private const val HTTPS_SCHEME = "https"
    private const val HTTP_SCHEME = "http"
    private const val ROUTE_SCHEME = "route"

    fun getRouteIntent(
        context: Context,
        routeString: String,
        flags: Int = 0,
        source: String? = null,
    ): Intent? {
        val route = Uri.parse(routeString)
        var intent: Intent? = null
        when (route.host) {
            ROUTE_POST_DETAIL -> {
                intent = getRouteToPostDetail(
                    context,
                    route,
                    source
                )
            }
            ROUTE_FEED -> {
                // navigation to feed
            }
            ROUTE_CREATE_POST -> {
                intent = getRouteToCreatePost(context, source)
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
        route: Uri,
        source: String?
    ): Intent {
        val postId = route.getQueryParameter(PARAM_POST_ID)
        val commentId = route.getQueryParameter(PARAM_COMMENT_ID)

        val builder = PostDetailExtras.Builder()
            .postId(postId.toString())
            .isEditTextFocused(false)
            .commentId(commentId)
            .source(source)
            .build()

        return PostDetailActivity.getIntent(
            context,
            builder
        )
    }

    // creates route for url and returns corresponding intent
    fun handleDeepLink(context: Context, url: String?): Intent? {
        val data = Uri.parse(url).normalizeScheme() ?: return null
        val firstPath = getRouteFromDeepLink(data) ?: return null
        return getRouteIntent(
            context,
            firstPath,
            0,
            source = LMFeedAnalytics.Source.DEEP_LINK
        )
    }

    //create route string as per uri
    private fun getRouteFromDeepLink(data: Uri?): String? {
        if (data == null) {
            return null
        }
        return when (data.pathSegments.firstOrNull()) {
            DEEP_LINK_POST -> {
                createPostDetailRoute(data)
            }
            else -> {
                createWebsiteRoute(data)
            }
        }
    }

    // https://<domain>/post/post_id=<post_id>
    private fun createPostDetailRoute(data: Uri): String? {
        val postId = data.getQueryParameter(PARAM_POST_ID) ?: return null
        return Uri.Builder()
            .scheme(ROUTE_SCHEME)
            .authority(ROUTE_POST_DETAIL)
            .appendQueryParameter(PARAM_POST_ID, postId)
            .build()
            .toString()
    }

    // creates a website route for the provided url
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
        context: Context,
        source: String?
    ): Intent {
        return LMFeedCreatePostActivity.getIntent(context, source)
    }
}