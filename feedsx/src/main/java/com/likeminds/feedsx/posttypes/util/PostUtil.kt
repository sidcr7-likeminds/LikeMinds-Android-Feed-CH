package com.likeminds.feedsx.posttypes.util

import com.likeminds.feedsx.utils.pluralize.model.WordAction
import com.likeminds.feedsx.utils.pluralize.pluralize
import com.likeminds.feedsx.utils.pluralize.singularize

object PostUtil {
    fun pluralizeOrCapitalize(postAsVariable: String, action: WordAction): String {
        return when (action) {
            WordAction.FIRST_LETTER_CAPITAL_SINGULAR -> {
                val singular = postAsVariable.singularize()
                singular.replaceFirstChar {
                    it.uppercase()
                }
            }

            WordAction.ALL_CAPITAL_SINGULAR -> {
                val singular = postAsVariable.singularize()
                singular.uppercase()
            }

            WordAction.ALL_SMALL_SINGULAR -> {
                val singular = postAsVariable.singularize()
                singular.lowercase()
            }

            WordAction.FIRST_LETTER_CAPITAL_PLURAL -> {
                val plural = postAsVariable.pluralize()
                plural.replaceFirstChar {
                    it.uppercase()
                }
            }

            WordAction.ALL_CAPITAL_PLURAL -> {
                val plural = postAsVariable.pluralize()
                plural.uppercase()
            }

            WordAction.ALL_SMALL_PLURAL -> {
                val plural = postAsVariable.pluralize()
                plural.lowercase()
            }
        }
    }
}