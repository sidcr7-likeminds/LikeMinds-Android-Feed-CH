package com.likeminds.feedsx.overflowmenu.model

import androidx.annotation.IntDef

const val DELETE_POST_MENU_ITEM_ID = 1
const val PIN_POST_MENU_ITEM_ID = 2
const val UNPIN_POST_MENU_ITEM_ID = 3
const val REPORT_POST_MENU_ITEM_ID = 4
const val EDIT_POST_MENU_ITEM_ID = 5
const val DELETE_COMMENT_MENU_ITEM_ID = 6
const val REPORT_COMMENT_MENU_ITEM_ID = 7
const val EDIT_COMMENT_MENU_ITEM_ID = 8

@IntDef(
    DELETE_POST_MENU_ITEM_ID,
    PIN_POST_MENU_ITEM_ID,
    UNPIN_POST_MENU_ITEM_ID,
    REPORT_POST_MENU_ITEM_ID,
    EDIT_POST_MENU_ITEM_ID,
    DELETE_COMMENT_MENU_ITEM_ID,
    REPORT_COMMENT_MENU_ITEM_ID,
    EDIT_COMMENT_MENU_ITEM_ID
)
@Retention(AnnotationRetention.SOURCE)
annotation class OverflowMenuItemId