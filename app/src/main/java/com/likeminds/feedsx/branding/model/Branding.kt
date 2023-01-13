package com.likeminds.feedsx.branding.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Branding(
//    @SerializedName("basic")
    val basic: BrandingBasic? = null,
//    @SerializedName("advanced")
    val advanced: BrandingAdvanced? = null
) : Parcelable