package com.likeminds.feedsx.utils

import android.content.Context
import android.content.res.Resources
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.branding.customview.snackbar.LikeMindsSnackbar

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

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.show() {
        visibility = View.VISIBLE
    }

    fun hideKeyboard(view: View) {
        val imm =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showShortToast(context: Context?, text: String?) {
        if (context == null || text.isNullOrEmpty()) return
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun FragmentManager.currentFragment(navHostId: Int): Fragment? {
        val navHostFragment = this.findFragmentById(navHostId) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    }

    fun String.getUrlIfExist(): String? {
        return try {
            val links: MutableList<String> = ArrayList()
            val matcher = Patterns.WEB_URL.matcher(this)
            while (matcher.find()) {
                val link = matcher.group()
                if (URLUtil.isValidUrl(link)) {
                    links.add(link)
                } else {
                    val newHttpsLink = "https://$link"
                    if (URLUtil.isValidUrl(newHttpsLink)) {
                        links.add(newHttpsLink)
                    }
                }
            }
            if (links.isNotEmpty()) {
                links.first()
            } else null
        } catch (e: Exception) {
            return null
        }
    }

    // shows short length snackbar
    fun showShortSnack(view: View, text: String?, anchorView: View? = null) {
        if (text.isNullOrEmpty()) return
        val snackBar = LikeMindsSnackbar.make(view, text)
        anchorView?.let {
            snackBar.setAnchorView(anchorView)
        }
        snackBar.show()
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
}