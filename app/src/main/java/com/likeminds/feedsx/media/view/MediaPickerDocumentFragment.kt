package com.likeminds.feedsx.media.view

import com.likeminds.feedsx.databinding.FragmentMediaPickerDocumentBinding
import com.likeminds.feedsx.utils.customview.BaseFragment

class MediaPickerDocumentFragment : BaseFragment<FragmentMediaPickerDocumentBinding>() {
    override fun getViewBinding(): FragmentMediaPickerDocumentBinding {
        return FragmentMediaPickerDocumentBinding.inflate(layoutInflater)
    }
}