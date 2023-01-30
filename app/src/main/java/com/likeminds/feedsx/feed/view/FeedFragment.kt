package com.likeminds.feedsx.feed.view

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.databinding.FragmentFeedBinding
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.feed.viewmodel.FeedViewModel
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment :
    BaseFragment<FragmentFeedBinding>(),
    PostAdapterListener {

    private val viewModel: FeedViewModel by viewModels()
    lateinit var mPostAdapter: PostAdapter

    companion object {

    }

    override fun getViewBinding(): FragmentFeedBinding {
        return FragmentFeedBinding.inflate(layoutInflater)
    }

    override fun setUpViews() {
        super.setUpViews()
        initUI()
        initToolbar()
    }

    private fun initUI() {
        //TODO: Set as per branding
        binding.isBrandingBasic = true

        initRecyclerView()
    }

    private fun initRecyclerView() {
        mPostAdapter = PostAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mPostAdapter
            show()
        }

        //TODO: Testing data
        val text =
            "My name is Siddharth Dubey ajksfbajshdbfjakshdfvajhskdfv kahsgdv hsdafkgv ahskdfgv b "
        mPostAdapter.add(
            PostViewData.Builder()
                .id("1")
                .user(UserViewData.Builder().name("Sid").customTitle("Admin").build())
                .text(text)
                .build()
        )
        mPostAdapter.add(
            PostViewData.Builder()
                .id("2")
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )
        mPostAdapter.add(
            PostViewData.Builder()
                .id("3")
                .user(UserViewData.Builder().name("Natesh").customTitle("Admin").build())
                .text(text)
                .build()
        )
        mPostAdapter.add(
            PostViewData.Builder()
                .attachments(
                    listOf(
                        AttachmentViewData.Builder()
                            .attachmentType(LINK)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .ogTags(
                                        LinkOGTags.Builder()
                                            .title("Youtube video")
                                            .image("https://i.ytimg.com/vi/EbyAoYaUcVo/hq720.jpg?sqp=-oaymwEcCNAFEJQDSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLDiI5bXtT71sC4IAnHiDAh52LxbFA")
                                            .url("https://www.youtube.com/watch?v=sAuQjwEl-Bo")
                                            .description("This is a youtube video")
                                            .build()
                                    )
                                    .build()
                            ).build()
                    )
                )
                .id("4")
                .user(UserViewData.Builder().name("Mahir").customTitle("Admin").build())
                .text(text)
                .build()
        )
        mPostAdapter.add(
            PostViewData.Builder()
                .attachments(
                    listOf(
                        AttachmentViewData.Builder()
                            .attachmentType(IMAGE)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .url("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQKEHgSbU751Z6Gn5FsbVMw7x_VFyKGwwHEEUiC9HtnKw&s")
                                    .build()
                            )
                            .build()
                    )
                )
                .id("5")
                .user(UserViewData.Builder().name("Natesh").customTitle("Admin").build())
                .text(text)
                .build()
        )
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        //if user is guest user hide, profile icon from toolbar
        binding.memberImage.isVisible = !isGuestUser

        //click listener -> open profile screen
        binding.memberImage.setOnClickListener {
            TODO("Not yet implemented")
        }

        binding.ivSearch.setOnClickListener {
            TODO("Not yet implemented")
        }
    }

    override fun updateSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .build()
            if (newViewData != null) {
                mPostAdapter.update(position, newViewData)
            }
        }
    }

    override fun pinPost() {
        TODO("Not yet implemented")
    }

    override fun savePost() {
        TODO("Not yet implemented")
    }

    override fun likePost() {
        TODO("Not yet implemented")
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        //TODO: Perform action on post's menu item selection
        Toast.makeText(context, "Post id :${postId}, Title :${title}", Toast.LENGTH_SHORT)
    }

    override fun onMultipleDocumentsExpanded(postData: PostViewData, position: Int) {
        if (position == mPostAdapter.items().size - 1) {
            binding.recyclerView.post {
                scrollToPositionWithOffset(position)
            }
        }

        mPostAdapter.update(
            position, postData.toBuilder().isExpanded(true).build()
        )
    }

    // opens likes screen when likes count is tapped.
    override fun showLikesScreen(postData: PostViewData) {
        val extras = LikesScreenExtras.Builder()
            .postId(postData.id)
            .likesCount(postData.likesCount)
            .build()
        LikesActivity.start(requireContext(), extras)
    }

    /**
     * Scroll to a position with offset from the top header
     * @param position Index of the item to scroll to
     */
    private fun scrollToPositionWithOffset(position: Int) {
        val px = if (binding.vTopBackground.height == 0) {
            (ViewUtils.dpToPx(75) * 1.5).toInt()
        } else {
            (binding.vTopBackground.height * 1.5).toInt()
        }
        (binding.recyclerView.layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(
            position,
            px
        )
    }
}