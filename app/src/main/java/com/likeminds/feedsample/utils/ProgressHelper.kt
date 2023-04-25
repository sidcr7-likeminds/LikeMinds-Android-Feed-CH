package com.likeminds.feedsample.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.likeminds.feedsample.R
import com.likeminds.feedsample.databinding.ProgressBarBinding

object ProgressHelper {
    fun showProgress(
        progressBarBinding: ProgressBarBinding,
        enableBackground: Boolean = false
    ) {
        if (enableBackground) {
            progressBarBinding.root.setBackgroundColor(
                ContextCompat.getColor(
                    progressBarBinding.root.context,
                    R.color.background
                )
            )
        } else {
            progressBarBinding.root.background = null
        }
        progressBarBinding.root.visibility = View.VISIBLE
        progressBarBinding.root.setOnClickListener { }
    }

    fun isVisible(progressBarBinding: ProgressBarBinding): Boolean {
        return progressBarBinding.root.visibility == View.VISIBLE
    }

    fun hideProgress(progressBarBinding: ProgressBarBinding) {
        progressBarBinding.root.visibility = View.GONE
    }
}