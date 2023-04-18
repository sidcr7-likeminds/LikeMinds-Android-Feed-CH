package com.likeminds.feedsx.post.edit.view

import androidx.fragment.app.viewModels
import com.likeminds.feedsx.databinding.FragmentEditPostBinding
import com.likeminds.feedsx.post.edit.model.EditPostExtras
import com.likeminds.feedsx.post.edit.view.EditPostActivity.Companion.EDIT_POST_EXTRAS
import com.likeminds.feedsx.post.edit.viewmodel.EditPostViewModel
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditPostFragment : BaseFragment<FragmentEditPostBinding>() {

    private val viewModel: EditPostViewModel by viewModels()

    private lateinit var editPostExtras: EditPostExtras

    override fun getViewBinding(): FragmentEditPostBinding {
        return FragmentEditPostBinding.inflate(layoutInflater)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(EDIT_POST_EXTRAS) == false) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        editPostExtras = arguments?.getParcelable(EDIT_POST_EXTRAS)!!
    }
}