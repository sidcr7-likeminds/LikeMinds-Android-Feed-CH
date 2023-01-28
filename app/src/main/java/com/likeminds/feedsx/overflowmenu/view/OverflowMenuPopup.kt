package com.likeminds.feedsx.overflowmenu.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.likeminds.feedsx.databinding.PopupOverflowMenuBinding
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapter
import com.likeminds.feedsx.overflowmenu.view.adapter.OverflowMenuAdapterListener
import com.likeminds.feedsx.overflowmenu.model.OverflowMenuItemViewData
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.isValidIndex

class OverflowMenuPopup(
    private val context: Context,
    private val overflowMenuAdapter: OverflowMenuAdapter
) : PopupWindow(context) {

    companion object {
        fun create(
            context: Context,
            listener: OverflowMenuAdapterListener
        ): OverflowMenuPopup {
            val overflowMenu = OverflowMenuPopup(
                context,
                OverflowMenuAdapter(listener)
            )
            overflowMenu.contentView = null
            return overflowMenu
        }
    }

    override fun setContentView(contentView: View?) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = PopupOverflowMenuBinding.inflate(inflater, null, false)
        setBackgroundDrawable(null)
        binding.rvOverflowMenu.adapter = overflowMenuAdapter
        super.setContentView(binding.root)

        height = ConstraintLayout.LayoutParams.WRAP_CONTENT
        width = ViewUtils.dpToPx(200)
        elevation = 10f
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setItems(items: List<OverflowMenuItemViewData>) {
        overflowMenuAdapter.replace(items)
    }

    fun update(item: OverflowMenuItemViewData) {
        val index = overflowMenuAdapter.items().indexOfFirst {
            (it as OverflowMenuItemViewData).title == item.title
        }
        if (index.isValidIndex()) {
            overflowMenuAdapter.update(index, item)
        }
    }
}