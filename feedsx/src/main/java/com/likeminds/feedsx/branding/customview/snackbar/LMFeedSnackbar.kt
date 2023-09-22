package com.likeminds.feedsx.branding.customview.snackbar

import android.view.*
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.likeminds.feedsx.R
import com.likeminds.feedsx.utils.ViewUtils.findSuitableParent

class LMFeedSnackbar(
    parent: ViewGroup,
    content: LMFeedSnackbarView
) : BaseTransientBottomBar<LMFeedSnackbar>(parent, content, content) {

    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {

        fun make(view: View, text: String): LMFeedSnackbar {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            val customView = LayoutInflater.from(view.context).inflate(
                R.layout.lm_feed_snackbar,
                parent,
                false
            ) as LMFeedSnackbarView

            customView.textView.text = text

            return LMFeedSnackbar(
                parent,
                customView
            )
        }
    }
}