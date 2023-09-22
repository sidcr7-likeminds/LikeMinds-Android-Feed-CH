package com.likeminds.feedsx.branding.model

class SetFeedBrandingRequest private constructor(
    val headerColor: String,
    val buttonsColor: String,
    val textLinkColor: String,
    val fonts: LMFeedFonts?
) {
    class Builder {
        private var headerColor: String = "#FFFFFF"
        private var buttonsColor: String = "#5046E5"
        private var textLinkColor: String = "#007AFF"
        private var fonts: LMFeedFonts? = null

        fun headerColor(headerColor: String) = apply { this.headerColor = headerColor }
        fun buttonsColor(buttonsColor: String) = apply { this.buttonsColor = buttonsColor }
        fun textLinkColor(textLinkColor: String) = apply { this.textLinkColor = textLinkColor }
        fun fonts(fonts: LMFeedFonts?) = apply { this.fonts = fonts }

        fun build() = SetFeedBrandingRequest(
            headerColor,
            buttonsColor,
            textLinkColor,
            fonts
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerColor(headerColor)
            .buttonsColor(buttonsColor)
            .textLinkColor(textLinkColor)
            .fonts(fonts)
    }
}