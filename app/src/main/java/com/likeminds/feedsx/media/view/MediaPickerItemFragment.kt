package com.likeminds.feedsx.media.view

import com.likeminds.feedsx.databinding.FragmentMediaPickerItemBinding
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MediaPickerItemFragment : BaseFragment<FragmentMediaPickerItemBinding>() {
    override fun getViewBinding(): FragmentMediaPickerItemBinding {
        return FragmentMediaPickerItemBinding.inflate(layoutInflater)
    }
}