package com.likeminds.feedsample.media.view.adapter

import com.likeminds.feedsample.media.model.MediaFolderViewData
import com.likeminds.feedsample.media.model.MediaViewData
import com.likeminds.feedsample.media.view.adapter.databinder.*
import com.likeminds.feedsample.utils.customview.BaseRecyclerAdapter
import com.likeminds.feedsample.utils.customview.ViewDataBinder
import com.likeminds.feedsample.utils.model.BaseViewType

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
    fun onFolderClicked(folderData: MediaFolderViewData) {}
    fun onMediaItemClicked(mediaViewData: MediaViewData, itemPosition: Int) {}
    fun onMediaItemLongClicked(mediaViewData: MediaViewData, itemPosition: Int) {}
    fun isMediaSelectionEnabled(): Boolean {
        return false
    }

    fun isMediaSelected(key: String): Boolean {
        return false
    }

    fun browseDocumentClicked() {}
    fun isMultiSelectionAllowed(): Boolean {
        return false
    }

}