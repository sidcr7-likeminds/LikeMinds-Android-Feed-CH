package com.likeminds.feedsx.overflowmenu.model

import androidx.annotation.StringDef

const val DELETE_POST_MENU_ITEM = "Delete Post"
const val PIN_POST_MENU_ITEM = "Pin this Post"
const val UNPIN_POST_MENU_ITEM = "Unpin this Post"
const val REPORT_POST_MENU_ITEM = "Report"
const val DELETE_COMMENT_MENU_ITEM = "Delete"
const val REPORT_COMMENT_MENU_ITEM = "Report"

@StringDef(
    DELETE_POST_MENU_ITEM,
    PIN_POST_MENU_ITEM,
    UNPIN_POST_MENU_ITEM,
    REPORT_POST_MENU_ITEM,
    DELETE_COMMENT_MENU_ITEM,
    REPORT_COMMENT_MENU_ITEM
)

@Retention(AnnotationRetention.SOURCE)
annotation class OverflowMenuItemTitle
