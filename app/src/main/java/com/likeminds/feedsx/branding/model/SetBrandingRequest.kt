package com.likeminds.feedsx.branding.model

class SetBrandingRequest private constructor(
    val headerColor: String,
    val buttonColor: String,
    val textLinkColor: String,
    val fonts: LMFonts?
) {
    class Builder {
        private var headerColor: String = "#FFFFFF"
        private var buttonColor: String = "#5046E5"
        private var textLinkColor: String = "#007AFF"
        private var fonts: LMFonts? = null

        fun headerColor(headerColor: String) = apply { this.headerColor = headerColor }
        fun buttonColor(buttonColor: String) = apply { this.buttonColor = buttonColor }
        fun textLinkColor(textLinkColor: String) = apply { this.textLinkColor = textLinkColor }
        fun fonts(fonts: LMFonts?) = apply { this.fonts = fonts }

        fun build() = SetBrandingRequest(
            headerColor,
            buttonColor,
            textLinkColor,
            fonts
        )
    }

    fun toBuilder(): Builder {
        return Builder().headerColor(headerColor)
            .buttonColor(buttonColor)
            .textLinkColor(textLinkColor)
            .fonts(fonts)
    }
}