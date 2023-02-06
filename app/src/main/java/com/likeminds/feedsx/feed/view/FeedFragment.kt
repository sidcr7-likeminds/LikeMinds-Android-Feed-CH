package com.likeminds.feedsx.feed.view

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.FragmentFeedBinding
import com.likeminds.feedsx.deletecontent.model.DELETE_CONTENT_TYPE_POST
import com.likeminds.feedsx.deletecontent.model.DeleteContentExtras
import com.likeminds.feedsx.deletecontent.view.DeleteContentDialogFragment
import com.likeminds.feedsx.feed.model.LikesScreenExtras
import com.likeminds.feedsx.feed.viewmodel.FeedViewModel
import com.likeminds.feedsx.post.detail.model.PostDetailExtras
import com.likeminds.feedsx.post.detail.view.PostDetailActivity
import com.likeminds.feedsx.post.view.CreatePostActivity
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.report.model.REPORT_TYPE_POST
import com.likeminds.feedsx.report.model.ReportExtras
import com.likeminds.feedsx.report.view.ReportActivity
import com.likeminds.feedsx.report.view.ReportSuccessDialog
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedFragment :
    BaseFragment<FragmentFeedBinding>(),
    PostAdapterListener,
    DeleteContentDialogFragment.DeleteContentDialogListener {

    private val viewModel: FeedViewModel by viewModels()
    private lateinit var mPostAdapter: PostAdapter
    private lateinit var alertDialog: AlertDialog

    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                ReportSuccessDialog("Message").show(
                    childFragmentManager,
                    ReportSuccessDialog.TAG
                )
            }
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
        initNewPostClick()
    }

    private fun initNewPostClick() {
        binding.newPostButton.setOnClickListener {
            CreatePostActivity.start(requireContext())
        }
    }

    private fun initRecyclerView() {
        mPostAdapter = PostAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mPostAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val isExtended = binding.newPostButton.isExtended

                    // Scroll down
                    if (dy > 20 && isExtended) {
                        binding.newPostButton.shrink()
                    }

                    // Scroll up
                    if (dy < -20 && !isExtended) {
                        binding.newPostButton.extend()
                    }

                    // At the top
                    if (!recyclerView.canScrollVertically(-1)) {
                        binding.newPostButton.extend()
                    }
                }
            })
            show()
        }

        //TODO: Remove Testing data
        addTestingData()
    }

    private fun addTestingData() {
        var text =
            "My name is Siddharth Dubey ajksfbajshdbfjakshdfvajhskdfv kahsgdv hsdafkgv ahskdfgv b "
        mPostAdapter.add(
            PostViewData.Builder()
                .id("1")
                .user(UserViewData.Builder().name("Sid").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("2")
                .likesCount(10)
                .commentsCount(4)
                .isLiked(true)
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
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
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
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
        text =
            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("6")
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("7")
                .user(UserViewData.Builder().name("Natesh").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("8")
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("9")
                .user(UserViewData.Builder().name("Natesh").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here', making it look like readable English. Many desktop publishing packages and web page editors now use Lorem Ipsum as their default model text, and a search for 'lorem ipsum' will uncover many web sites still in their infancy."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("10")
                .user(UserViewData.Builder().name("Ishaan").customTitle("Admin").build())
                .text(text)
                .build()
        )
        text =
            "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
        mPostAdapter.add(
            PostViewData.Builder()
                .id("11")
                .user(UserViewData.Builder().name("Natesh").customTitle("Admin").build())
                .text(text)
                .build()
        )
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        //click listener -> open profile screen
        binding.memberImage.setOnClickListener {
            //TODO: On member Image click
        }

        binding.ivSearch.setOnClickListener {
            //TODO: perform search
        }
    }

    // processes delete post request
    private fun deletePost(postId: String) {
        //TODO: set isAdmin
        val isAdmin = true
        if (isAdmin) {
            val deleteContentExtras = DeleteContentExtras.Builder()
                .contentId(postId)
                .contentType(DELETE_CONTENT_TYPE_POST)
                .build()
            DeleteContentDialogFragment.showDialog(
                childFragmentManager,
                deleteContentExtras
            )
        } else {
            showDeletePostDialog()
        }
    }

    // shows delete post dialog when user deletes their own post
    private fun showDeletePostDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(getString(R.string.delete_post_message))
            .setTitle(getString(R.string.delete_post_question))
            .setCancelable(true)
            .setPositiveButton("DELETE") { _, _ ->

            }.setNegativeButton("CANCEL") { _, _ ->
                alertDialog.dismiss()
            }
        //Creating dialog box
        alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_40))
    }

    // Processes report action on post
    private fun reportPost(postId: String) {
        //create extras for [ReportActivity]
        val reportExtras = ReportExtras.Builder()
            .dataId(postId)
            .type(REPORT_TYPE_POST)
            .build()

        //get Intent for [ReportActivity]
        val intent = ReportActivity.getIntent(requireContext(), reportExtras)

        //start [ReportActivity] and check for result
        reportPostLauncher.launch(intent)
    }

    override fun updateSeenFullContent(position: Int, alreadySeenFullContent: Boolean) {
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .alreadySeenFullContent(alreadySeenFullContent)
                .build()
            mPostAdapter.update(position, newViewData)
        }
    }

    override fun pinPost() {
        //TODO: pin post
    }

    override fun savePost() {
        //TODO: save post
    }

    override fun likePost() {
        //TODO: like post
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        //TODO: Perform action on post's menu item selection
        // Testing data
        when (title) {
            "Report" -> {
                reportPost(postId)
            }
            "Delete" -> {
                deletePost(postId)
            }
        }
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

    // opens likes screen when likes count is clicked.
    override fun showLikesScreen(postData: PostViewData) {
        val likesScreenExtras = LikesScreenExtras.Builder()
            .postId(postData.id)
            .likesCount(postData.likesCount)
            .build()
        LikesActivity.start(requireContext(), likesScreenExtras)
    }

    //opens post detail screen when add comment/comments count is clicked
    override fun comment(postData: PostViewData) {
        val postDetailExtras = PostDetailExtras.Builder()
            .postId(postData.id)
            .isEditTextFocused(true)
            .commentsCount(postData.commentsCount)
            .build()
        PostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //opens post detail screen when post content is clicked
    //TODO: confirm and trigger
    override fun postDetails(postData: PostViewData) {
        val postDetailExtras = PostDetailExtras.Builder()
            .postId(postData.id)
            .isEditTextFocused(false)
            .commentsCount(postData.commentsCount)
            .build()
        PostDetailActivity.start(requireContext(), postDetailExtras)
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

    override fun deleteContent(
        deleteContentExtras: DeleteContentExtras,
        reportTagId: String,
        reason: String
    ) {
        // TODO: call api
    }
}