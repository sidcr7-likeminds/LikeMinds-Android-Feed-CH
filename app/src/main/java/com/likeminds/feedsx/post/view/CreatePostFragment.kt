package com.likeminds.feedsx.post.view

import androidx.fragment.app.viewModels
import com.likeminds.feedsx.databinding.FragmentCreatePostBinding
import com.likeminds.feedsx.post.viewmodel.CreatePostViewModel
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>() {

    private val viewModel: CreatePostViewModel by viewModels()

    override fun getViewBinding(): FragmentCreatePostBinding {
        return FragmentCreatePostBinding.inflate(layoutInflater)
    }
}