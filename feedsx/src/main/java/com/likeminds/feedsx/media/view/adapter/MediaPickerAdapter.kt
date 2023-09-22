package com.likeminds.feedsx.media.view.adapter

import com.likeminds.feedsx.media.model.MediaFolderViewData
import com.likeminds.feedsx.media.model.MediaViewData
import com.likeminds.feedsx.media.view.adapter.databinder.*
import com.likeminds.feedsx.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsx.utils.customview.ViewDataBinder
import com.likeminds.feedsx.utils.model.BaseViewType

class MediaPickerAdapter constructor(
    val listener: MediaPickerAdapterListener
) : BaseRecyclerAdapter<BaseViewType>() {

    init {
        initViewDataBinders()
    }

    override fun getSupportedViewDataBinder(): MutableList<ViewDataBinder<*, *>> {
        val viewDataBinders = ArrayList<ViewDataBinder<*, *>>(5)

        val lmFeedMediaPickerFolderItemViewDataBinder =
            LMFeedMediaPickerFolderItemViewDataBinder(listener)
        viewDataBinders.add(lmFeedMediaPickerFolderItemViewDataBinder)

        val lmFeedMediaPickerHeaderItemViewDataBinder = LMFeedMediaPickerHeaderItemViewDataBinder()
        viewDataBinders.add(lmFeedMediaPickerHeaderItemViewDataBinder)

        val lmFeedMediaPickerSingleItemViewDataBinder =
            LMFeedMediaPickerSingleItemViewDataBinder(listener)
        viewDataBinders.add(lmFeedMediaPickerSingleItemViewDataBinder)

        val lmFeedMediaPickerBrowseItemViewDataBinder =
            LMFeedMediaPickerBrowseItemViewDataBinder(listener)
        viewDataBinders.add(lmFeedMediaPickerBrowseItemViewDataBinder)

        val lmFeedMediaPickerDocumentItemViewDataBinder =
            LMFeedMediaPickerDocumentItemViewDataBinder(listener)
        viewDataBinders.add(lmFeedMediaPickerDocumentItemViewDataBinder)

        return viewDataBinders
    }

}

interface MediaPickerAdapterListener {
    fun onFolderClicked(folderData: MediaFolderViewData) {
        //triggered when a folder is clicked and opened
    }
    fun onMediaItemClicked(mediaViewData: MediaViewData, itemPosition: Int) {
        //triggered when a single media is selected
    }
    fun onMediaItemLongClicked(mediaViewData: MediaViewData, itemPosition: Int) {
        //triggered when a single media is selected with long press
    }
    fun isMediaSelectionEnabled(): Boolean {
        return false
    }

    fun isMediaSelected(key: String): Boolean {
        return false
    }

    fun browseDocumentClicked() {
        //triggered when a user wants to open a default media browser
    }
    fun isMultiSelectionAllowed(): Boolean {
        return false
    }

}