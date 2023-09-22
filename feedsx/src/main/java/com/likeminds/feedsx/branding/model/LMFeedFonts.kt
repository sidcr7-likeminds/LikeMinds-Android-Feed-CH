package com.likeminds.feedsx.branding.model

/**
 * variables here, hold path for the fonts
 **/
class LMFeedFonts private constructor(
    val regular: String,
    val medium: String,
    val bold: String
) {
    class Builder {
        private var regular: String = ""
        private var medium: String = ""
        private var bold: String = ""

        fun regular(regular: String) = apply { this.regular = regular }
        fun medium(medium: String) = apply { this.medium = medium }
        fun bold(bold: String) = apply { this.bold = bold }

        fun build() = LMFeedFonts(
            regular,
            medium,
            bold
        )
    }

    fun toBuilder(): Builder {
        return Builder().regular(regular)
            .medium(medium)
            .bold(bold)
    }
}
