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

        val mediaPickerFolderItemViewDataBinder = MediaPickerFolderItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerFolderItemViewDataBinder)

        val mediaPickerHeaderItemViewDataBinder = MediaPickerHeaderItemViewDataBinder()
        viewDataBinders.add(mediaPickerHeaderItemViewDataBinder)

        val mediaPickerSingleItemViewDataBinder = MediaPickerSingleItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerSingleItemViewDataBinder)

        val mediaPickerBrowseItemViewDataBinder = MediaPickerBrowseItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerBrowseItemViewDataBinder)

        val mediaPickerDocumentItemViewDataBinder = MediaPickerDocumentItemViewDataBinder(listener)
        viewDataBinders.add(mediaPickerDocumentItemViewDataBinder)

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