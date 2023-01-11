package com.likeminds.feedsx.utils.branding

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class BrandingAdvanced(
//    @SerializedName("header_colour")
    val headerColor: String? = null,
//    @SerializedName("buttons_icons_colour")
    val buttonsIconsColor: String? = null,
//    @SerializedName("text_links_colour")
    val textLinksColor: String? = null
) : Parcelable