package com.likeminds.feedsx.utils

import android.view.View
import androidx.core.content.ContextCompat
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ProgressBarBinding

object ProgressHelper {
    fun showProgress(
        progressBarBinding: ProgressBarBinding,
        enableBackground: Boolean = false
    ) {
        progressBarBinding.root.apply {
            if (enableBackground) {
                setBackgroundColor(
                    ContextCompat.getColor(
                        progressBarBinding.root.context,
                        R.color.background
                    )
                )
            } else {
                background = null
            }
            visibility = View.VISIBLE
            setOnClickListener { }
        }
    }

    fun isVisible(progressBarBinding: ProgressBarBinding): Boolean {
        return progressBarBinding.root.visibility == View.VISIBLE
    }

    fun hideProgress(progressBarBinding: ProgressBarBinding) {
        progressBarBinding.root.visibility = View.GONE
    }
}