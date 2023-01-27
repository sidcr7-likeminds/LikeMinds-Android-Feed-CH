package com.likeminds.feedsx.feed.view

import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.databinding.FragmentLikesBinding
import com.likeminds.feedsx.feed.view.LikesActivity.Companion.LIKES_SCREEN_EXTRAS
import com.likeminds.feedsx.feed.view.adapter.LikesScreenAdapter
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.emptyExtrasException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesFragment : BaseFragment<FragmentLikesBinding>() {

    lateinit var mLikesScreenAdapter: LikesScreenAdapter

    private lateinit var extras: LikesScreenExtras

    companion object {
        private const val TAG = "Likes Screen"
    }

    override fun getViewBinding(): FragmentLikesBinding {
        return FragmentLikesBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mLikesScreenAdapter = LikesScreenAdapter()
        binding.rvLikes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mLikesScreenAdapter
            show()
        }

        Log.d("TAG", extras.postId + " " + extras.likesCount)

        //TODO: Testing data

    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        extras = args?.getParcelable(LIKES_SCREEN_EXTRAS) ?: throw emptyExtrasException(TAG)
    }
}