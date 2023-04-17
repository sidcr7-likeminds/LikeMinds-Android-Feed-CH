package com.likeminds.feedsx.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.likeminds.feedsx.LMAnalytics
import com.likeminds.feedsx.feed.view.MainActivity
import com.likeminds.feedsx.post.create.view.CreatePostActivity
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity

object Route {

    const val ROUTE_POST_DETAIL = "post_detail"
    const val ROUTE_POST = "post"
    const val ROUTE_FEED = "feed"
    const val ROUTE_CREATE_POST = "create_post"
    const val PARAM_POST_ID = "post_id"
    const val ROUTE_BROWSER = "browser"
    const val PARAM_COMMENT_ID = "comment_id"
    const val ROUTE_MAIN = "main"

    private const val DEEP_LINK_POST = "post"
    private const val DEEP_LINK_SCHEME = "sample"

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
            ROUTE_POST -> {
                intent = getRouteToPostDetail(
                    context,
                    route,
                    source
                )
            }
            ROUTE_POST_DETAIL -> {
                intent = getRouteToPostDetail(
                    context,
                    route,
                    source
                )
            }
            ROUTE_FEED -> {
                //TODO: navigation to feed fragment
            }
            ROUTE_CREATE_POST -> {
                intent = getRouteToCreatePost(context, source)
            }
            ROUTE_BROWSER -> {
                intent = getRouteToBrowser(route)
            }
            ROUTE_MAIN ->
                intent = getRouteToMain(context)
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

    //TODO: navigation to feed fragment

    // route://feed?type=
//    private fun getRouteToFeed(
//        context: Context,
//        routeString: String,
//    ): Intent {
//
//    }


    // creates route for url and returns corresponding intent
    fun handleDeepLink(context: Context, url: String?): Intent? {
        val data = Uri.parse(url).normalizeScheme() ?: return null
        val firstPath = getRouteFromDeepLink(data) ?: return null
        return getRouteIntent(
            context,
            firstPath,
            0,
            source = LMAnalytics.Source.DEEP_LINK
        )
    }

    //create route string as per uri
    private fun getRouteFromDeepLink(data: Uri?): String? {
        val host = data?.host ?: return null
        val firstPathSegment = data.pathSegments.firstOrNull()
        return when (firstPathSegment) {
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
        Log.d("PUI", "getRouteFromDeepLink: $postId scheme: ${data.scheme}")
        return Uri.Builder()
            .scheme(ROUTE_SCHEME)
            .authority(ROUTE_POST)
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
        return CreatePostActivity.getIntent(context, source)
    }

    //route://main
    fun getRouteToMain(
        context: Context,
    ): Intent {
        return MainActivity.getIntent(
            context
        )
    }
}