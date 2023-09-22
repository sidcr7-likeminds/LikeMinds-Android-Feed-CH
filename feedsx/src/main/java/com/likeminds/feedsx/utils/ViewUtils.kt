package com.likeminds.feedsx.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.likeminds.feedsx.LMFeedAnalytics
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.customview.snackbar.LMFeedSnackbar
import com.likeminds.feedsx.utils.model.*

//view related utils class
object ViewUtils {
    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun spToPx(sp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            Resources.getSystem().displayMetrics
        )
    }

    fun hideKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // shoes bounce animation on the provided view
    fun showBounceAnim(context: Context, view: View) {
        // bounce animation for save button
        val saveBounceAnim: Animation by lazy {
            AnimationUtils.loadAnimation(
                context,
                R.anim.lm_feed_bounce
            )
        }

        saveBounceAnim.interpolator = LMFeedBounceInterpolator(0.2, 20.0)
        view.startAnimation(saveBounceAnim)
    }

    // returns shimmer drawable
    fun getShimmer(): ShimmerDrawable {
        val shimmer =
            Shimmer.AlphaHighlightBuilder() // The attributes for a ShimmerDrawable is set by this builder
                .setDuration(1800) // how long the shimmering animation takes to do one full sweep
                .setBaseAlpha(0.85f) //the alpha of the underlying children
                .setHighlightAlpha(0.7f) // the shimmer alpha amount
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build()

        // This is the placeholder for the imageView
        val shimmerDrawable = ShimmerDrawable().apply {
            setShimmer(shimmer)
        }
        return shimmerDrawable
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun showShortToast(context: Context?, text: String?) {
        if (context == null || text.isNullOrEmpty()) return
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    // shows short toast with "Something went wrong!" message
    fun showSomethingWentWrongToast(context: Context) {
        showShortToast(context, context.getString(R.string.something_went_wrong))
    }

    // shows short toast with error message
    fun showErrorMessageToast(context: Context, errorMessage: String?) {
        showShortToast(context, errorMessage ?: context.getString(R.string.something_went_wrong))
    }

    fun FragmentManager.currentFragment(navHostId: Int): Fragment? {
        val navHostFragment = this.findFragmentById(navHostId) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    }

    fun String.isValidUrl(): Boolean {
        if (this.isEmpty()) {
            return false
        }
        return Patterns.WEB_URL.matcher(this).matches()
    }

    // shows short length snackbar
    fun showShortSnack(view: View, text: String?, anchorView: View? = null) {
        if (text.isNullOrEmpty()) return
        val snackBar = LMFeedSnackbar.make(view, text)
        anchorView?.let {
            snackBar.setAnchorView(anchorView)
        }
        snackBar.show()
    }

    // returns type of post for analytics from the viewType of post
    fun getPostTypeFromViewType(postViewType: Int?): String {
        return when (postViewType) {
            ITEM_POST_TEXT_ONLY -> {
                LMFeedAnalytics.Keys.POST_TYPE_TEXT
            }

            ITEM_POST_SINGLE_IMAGE -> {
                LMFeedAnalytics.Keys.POST_TYPE_IMAGE
            }

            ITEM_POST_SINGLE_VIDEO -> {
                LMFeedAnalytics.Keys.POST_TYPE_VIDEO
            }

            ITEM_POST_DOCUMENTS -> {
                LMFeedAnalytics.Keys.POST_TYPE_DOCUMENT
            }

            ITEM_POST_MULTIPLE_MEDIA -> {
                LMFeedAnalytics.Keys.POST_TYPE_IMAGE_VIDEO
            }

            ITEM_POST_LINK -> {
                LMFeedAnalytics.Keys.POST_TYPE_LINK
            }

            else -> {
                LMFeedAnalytics.Keys.POST_TYPE_TEXT
            }
        }
    }

    //find parent for a particular view
    fun View?.findSuitableParent(): ViewGroup? {
        var view = this
        var fallback: ViewGroup? = null
        do {
            if (view is CoordinatorLayout) {
                // We've found a CoordinatorLayout, use it
                return view
            } else if (view is FrameLayout) {
                if (view.id == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return view
                } else {
                    // It's not the content view but we'll use it as our fallback
                    fallback = view
                }
            }

            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                val parent = view.parent
                view = if (parent is View) parent else null
            }
        } while (view != null)

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback
    }

    // adds an asterisk in textView for mandatory field
    fun getMandatoryAsterisk(
        title: String,
        tvTitle: TextView
    ) {
        val mandatoryAsterisk = SpannableString("$title*")
        mandatoryAsterisk.setSpan(
            ForegroundColorSpan(
                Color.RED
            ),
            title.length,
            title.length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvTitle.text = mandatoryAsterisk
    }

    // adds an asterisk for mandatory fields in the hint
    fun getMandatoryAsterisk(
        title: String,
        etPostTitle: EditText
    ) {
        val mandatoryAsterisk = SpannableString("$title*")
        mandatoryAsterisk.setSpan(
            ForegroundColorSpan(
                Color.RED
            ),
            title.length,
            title.length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        etPostTitle.hint = mandatoryAsterisk
    }
}