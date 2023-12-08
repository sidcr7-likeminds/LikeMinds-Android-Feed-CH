package com.likeminds.feedsx.utils.databinding

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.likeminds.feedsx.utils.ViewUtils

object ImageBindingUtil {

    @JvmStatic
    fun loadImageUsingBinding(
        view: View,
        url: String?,
        drawable: Drawable? = null,
        placeholder: Drawable? = null,
        circle: Boolean? = false,
        showGreyScale: Boolean? = false,
        cornerRadius: Int? = null,
        centerCrop: Boolean? = false
    ) {
        var builder = when {
            url != null -> {
                Glide.with(view).load(url)
            }

            drawable != null -> {
                Glide.with(view).load(drawable).placeholder(placeholder).error(placeholder)
            }

            else -> {
                return
            }
        }
        if (circle == true) {
            builder = builder.circleCrop()
        }
        if (placeholder != null) {
            builder = builder.placeholder(placeholder).error(placeholder)
        }

        if (cornerRadius != null && cornerRadius > 0) {
            builder = builder.transform(CenterCrop(), RoundedCorners(cornerRadius))
        } else if (centerCrop == true) {
            builder = builder.transform(CenterCrop())
        }

        if (view is ImageView) builder.into(view)

        if (showGreyScale == true) {
            createImageFilter(view)
        }
    }

    /**
     * Use it to load images programmatically
     * @param view ImageView
     * @param file Supported types -> String, Uri, Drawable, Int resource, Bitmap
     * @param placeholder Placeholder and Error for this request | Supported types -> Drawable, Int resource
     * @param isCircle Is circle crop needed
     * @param cornerRadius Corner radius in dp.
     * @param showGreyScale Is greyscale image needed
     */
    @JvmStatic
    fun loadImage(
        view: ImageView,
        file: Any?,
        placeholder: Any? = null,
        isCircle: Boolean = false,
        cornerRadius: Int = 0,
        showGreyScale: Boolean = false,
        objectKey: Any? = null,
        onFailed: (() -> Unit)? = null
    ) {
        if ((file == null && placeholder == null)
            || (file != null && file !is String &&
                    file !is Uri && file !is Drawable &&
                    file !is Int && file !is Bitmap)
        ) {
            return
        }
        if (isValidContextForGlide(view.context)) {
            var builder = Glide.with(view).load(file)

            //Signature
            if (objectKey != null) {
                builder = builder.signature(ObjectKey(objectKey))
            }

            //Set the placeholder
            if (placeholder != null && placeholder is Int) {
                builder = builder.placeholder(placeholder).error(placeholder)
            } else if (placeholder != null && placeholder is Drawable) {
                builder = builder.placeholder(placeholder).error(placeholder)
            }
            if (isCircle) {
                builder = builder.circleCrop()
            }

            if (cornerRadius > 0) {
                builder = builder.transform(
                    CenterCrop(),
                    RoundedCorners(ViewUtils.dpToPx(cornerRadius))
                )
            }

            builder.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    if (onFailed != null) {
                        onFailed()
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
                .into(view)
            if (showGreyScale) {
                createImageFilter(view)
            } else {
                view.clearColorFilter()
            }
        }
    }

    private fun isValidContextForGlide(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity && isActivityDestroyedOrFinishing(context)) {
            return false
        }
        return true
    }

    private fun isActivityDestroyedOrFinishing(activity: Activity): Boolean {
        return activity.isDestroyed || activity.isFinishing
    }

    private fun createImageFilter(view: View) {
        val colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
            setSaturation(0f)
        })
        if (view is ImageView) {
            view.colorFilter = colorFilter
        }
    }
}