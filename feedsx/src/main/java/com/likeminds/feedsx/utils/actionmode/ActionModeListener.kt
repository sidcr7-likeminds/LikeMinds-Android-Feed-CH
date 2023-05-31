package com.likeminds.feedsx.utils.actionmode

import android.view.Menu
import android.view.MenuItem

internal interface ActionModeListener<T> {
    fun onActionItemClick(item: MenuItem?)
    fun onActionItemUpdate(item: Menu?, actionModeData: T?) {
        //triggered when action mode header is updated
    }
    fun onActionModeDestroyed()
}