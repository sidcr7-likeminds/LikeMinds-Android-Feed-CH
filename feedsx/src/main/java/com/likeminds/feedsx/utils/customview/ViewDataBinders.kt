package com.likeminds.feedsx.utils.customview

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.likeminds.feedsx.utils.model.BaseViewType
import com.likeminds.feedsx.utils.model.ViewType

abstract class ViewDataBinder<V : ViewDataBinding, T : BaseViewType> {

    @get:ViewType
    abstract val viewType: Int

    abstract fun createBinder(parent: ViewGroup): V

    abstract fun bindData(binding: V, data: T, position: Int)

    fun bindData() {
        //This function can be called in case to handle inflation of data dependent on `data`
    }
}