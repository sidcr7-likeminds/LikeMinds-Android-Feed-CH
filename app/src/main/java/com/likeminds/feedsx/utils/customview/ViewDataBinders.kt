package com.likeminds.feedsx.utils.customview

import android.os.Bundle
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ViewType

abstract class ViewDataBinder<V : ViewDataBinding, T : BaseViewType> {

    @get:ViewType
    abstract val viewType: Int

    abstract fun createBinder(parent: ViewGroup): V

    abstract fun bindData(binding: V, data: T, position: Int)

    fun bindData(binding: V, data: Bundle, position: Int) {}

    protected open fun drawPrimaryColor(binding: V, color: Int) {}

    protected open fun drawAdvancedColor(
        binding: V,
        headerColor: Int,
        buttonsIconsColor: Int,
        textLinksColor: Int,
    ) {
    }
}