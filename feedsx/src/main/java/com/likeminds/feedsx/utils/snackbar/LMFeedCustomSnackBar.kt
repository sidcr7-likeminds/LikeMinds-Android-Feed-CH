package com.likeminds.feedsx.utils.snackbar

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.likeminds.feedsx.databinding.LmFeedLayoutNoInternetBinding
import com.likeminds.feedsx.databinding.LmFeedLayoutSnackbarMessageBinding
import com.likeminds.feedsx.utils.ViewUtils.hide
import javax.inject.Inject

class LMFeedCustomSnackBar @Inject constructor() {

    private var snackBar: Snackbar? = null

    fun showNoInternet(
        view: View,
        dismiss: (() -> Unit)? = null,
    ) {
        createSnackBar(view, Snackbar.LENGTH_INDEFINITE)

        val binding = LmFeedLayoutNoInternetBinding.inflate(LayoutInflater.from(view.context))
        //set the custom view binding to snack bar
        (snackBar?.view as? Snackbar.SnackbarLayout)?.addView(binding.root)

        binding.ivClose.setOnClickListener {
            snackBar?.dismiss()
            if (dismiss != null) {
                dismiss()
            }
        }

        //when snack bar dismisses, set snack bar variable to null
        snackBar?.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                snackBar = null
            }
        })

        snackBar?.show()
    }

    fun showMessage(
        view: View,
        text: String?,
        hideIcon: Boolean? = false,
        duration: Int = Snackbar.LENGTH_SHORT,
    ) {
        createSnackBar(view, duration)
        val binding = LmFeedLayoutSnackbarMessageBinding.inflate(LayoutInflater.from(view.context))
        //set the custom view binding to snack bar
        (snackBar?.view as? Snackbar.SnackbarLayout)?.addView(binding.root)
        binding.tvMessage.text = text
        if (hideIcon == true) {
            binding.messageIcon.hide()
        }
        snackBar?.addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                snackBar = null
            }
        })
        snackBar?.show()
    }

    private fun createSnackBar(view: View, duration: Int) {
        //dismiss old snack bar is it's showing
        snackBar?.dismiss()
        //assign a new snack bar
        snackBar = Snackbar.make(view, "", duration)
        //remove default view
        (snackBar?.view as? Snackbar.SnackbarLayout)?.removeAllViews()
        //set background color to transparent else it shows grey
        snackBar?.view?.setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
    }

}