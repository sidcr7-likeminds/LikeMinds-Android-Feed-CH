package com.likeminds.feedsx.utils.branding

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class BrandingBasic(
//    @SerializedName("primary_colour")
    val primaryColor: String? = null
) : Parcelable