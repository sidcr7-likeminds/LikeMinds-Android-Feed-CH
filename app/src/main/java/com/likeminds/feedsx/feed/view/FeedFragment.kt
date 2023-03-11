package com.likeminds.feedsx.feed.view

import android.app.Activity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.databinding.FragmentFeedBinding
import com.likeminds.feedsx.delete.model.DELETE_TYPE_POST
import com.likeminds.feedsx.delete.model.DeleteExtras
import com.likeminds.feedsx.delete.view.DeleteAlertDialogFragment
import com.likeminds.feedsx.delete.view.DeleteDialogFragment
import com.likeminds.feedsx.feed.model.LikesScreenExtras
import com.likeminds.feedsx.feed.viewmodel.FeedViewModel
import com.likeminds.feedsx.notificationfeed.view.NotificationFeedActivity
import com.likeminds.feedsx.overflowmenu.model.DELETE_POST_MENU_ITEM
import com.likeminds.feedsx.overflowmenu.model.PIN_POST_MENU_ITEM
import com.likeminds.feedsx.overflowmenu.model.REPORT_POST_MENU_ITEM
import com.likeminds.feedsx.overflowmenu.model.UNPIN_POST_MENU_ITEM
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
import com.likeminds.feedsx.utils.EndlessRecyclerScrollListener
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.show
import com.likeminds.feedsx.utils.customview.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FeedFragment :
    BaseFragment<FragmentFeedBinding>(),
    PostAdapterListener,
    DeleteDialogFragment.DeleteDialogListener,
    DeleteAlertDialogFragment.DeleteAlertDialogListener {

    private val viewModel: FeedViewModel by viewModels()

    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    lateinit var mPostAdapter: PostAdapter

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
        initSwipeRefreshLayout()
        initNewPostClick()
    }

    private fun initNewPostClick() {
        binding.newPostButton.setOnClickListener {
            CreatePostActivity.start(requireContext())
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(context)
        mPostAdapter = PostAdapter(this)
        binding.recyclerView.apply {
            layoutManager = linearLayoutManager
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
            if (itemAnimator is SimpleItemAnimator)
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            show()
        }

        attachScrollListener(
            binding.recyclerView,
            linearLayoutManager
        )

        //TODO: Remove Testing data
        addTestingData()
    }

    private fun addTestingData() {
        var text =
            "My <<Ankit Garg|route://member/1278>> name is Siddharth Dubey ajksfbajshdbfjakshdfvajhskdfv kahsgdv hsdafkgv ahskdfgv b "
        mPostAdapter.add(
            PostViewData.Builder()
                .id("1")
                .user(UserViewData.Builder().name("Sid").customTitle("Admin").build())
                .text(text)
                .fromPostSaved(false)
                .fromPostLiked(false)
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

    // initializes swipe refresh layout and sets refresh listener
    private fun initSwipeRefreshLayout() {
        mSwipeRefreshLayout = binding.swipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeColors(
            BrandingData.getButtonsColor(),
        )

        mSwipeRefreshLayout.setOnRefreshListener {
            mSwipeRefreshLayout.isRefreshing = true
            fetchRefreshedData()
        }
    }

    //TODO: Call api and refresh the feed data
    private fun fetchRefreshedData() {
        //TODO: testing data

        val text =
            "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn't anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition, injected humour, or non-characteristic words etc."
        mPostAdapter.add(
            0,
            PostViewData.Builder()
                .attachments(
                    listOf(
                        AttachmentViewData.Builder()
                            .attachmentType(IMAGE)
                            .attachmentMeta(
                                AttachmentMetaViewData.Builder()
                                    .url("https://www.shutterstock.com/image-vector/sample-red-square-grunge-stamp-260nw-338250266.jpg")
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
        mSwipeRefreshLayout.isRefreshing = false
    }

    //attach scroll listener for pagination
    private fun attachScrollListener(
        recyclerView: RecyclerView,
        layoutManager: LinearLayoutManager
    ) {
        recyclerView.addOnScrollListener(object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                // TODO: add logic
            }

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
    }

    private fun initToolbar() {
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        //click listener -> open profile screen
        binding.memberImage.setOnClickListener {
            //TODO: On member Image click
        }

        binding.ivNotification.setOnClickListener {
            NotificationFeedActivity.start(requireContext())
        }

        binding.ivSearch.setOnClickListener {
            //TODO: perform search
        }

        //TODO: testing data. add this while observing data
        binding.tvNotificationCount.text = "10"
    }

    // processes delete post request
    private fun deletePost(postId: String) {
        //TODO: set isAdmin
        val isAdmin = false
        val deleteExtras = DeleteExtras.Builder()
            .entityId(postId)
            .entityType(DELETE_TYPE_POST)
            .build()
        if (isAdmin) {
            DeleteDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        } else {
            // when user deletes their own entity
            DeleteAlertDialogFragment.showDialog(
                childFragmentManager,
                deleteExtras
            )
        }
    }

    // Processes report action on post
    private fun reportPost(postId: String) {
        //create extras for [ReportActivity]
        val reportExtras = ReportExtras.Builder()
            .entityId(postId)
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
                .fromPostSaved(false)
                .fromPostLiked(false)
                .build()
            mPostAdapter.update(position, newViewData)
        }
    }

    // TODO: add fromPostSaved key while adding post data to adapter
    override fun savePost(position: Int) {
        //TODO: save post
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .fromPostSaved(true)
                .build()
            mPostAdapter.update(position, newViewData)
        }
    }

    // TODO: add fromPostLiked key while adding post data to adapter
    override fun likePost(position: Int) {
        //TODO: like post
        val item = mPostAdapter[position]
        if (item is PostViewData) {
            val newViewData = item.toBuilder()
                .fromPostLiked(true)
                .build()
            mPostAdapter.update(position, newViewData)
        }
    }

    override fun onPostMenuItemClicked(postId: String, title: String) {
        when (title) {
            DELETE_POST_MENU_ITEM -> {
                deletePost(postId)
            }
            REPORT_POST_MENU_ITEM -> {
                reportPost(postId)
            }
            PIN_POST_MENU_ITEM -> {
                // TODO: pin post
            }
            UNPIN_POST_MENU_ITEM -> {
                // TODO: unpin post
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
            position,
            postData.toBuilder()
                .isExpanded(true)
                .fromPostSaved(false)
                .fromPostLiked(false)
                .build()
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
    override fun comment(postId: String) {
        val postDetailExtras = PostDetailExtras.Builder()
            .postId(postId)
            .isEditTextFocused(true)
            .build()
        PostDetailActivity.start(requireContext(), postDetailExtras)
    }

    //opens post detail screen when post content is clicked
    override fun postDetail(postData: PostViewData) {
        val postDetailExtras = PostDetailExtras.Builder()
            .postId(postData.id)
            .isEditTextFocused(false)
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

    // callback when self post is deleted by user
    override fun delete(deleteExtras: DeleteExtras) {
        // TODO: delete post by user
        ViewUtils.showShortToast(
            requireContext(),
            getString(R.string.post_deleted)
        )
    }

    // callback when other's post is deleted by CM
    override fun delete(deleteExtras: DeleteExtras, reportTagId: String, reason: String) {
        // TODO: delete post by admin
        ViewUtils.showShortToast(
            requireContext(),
            getString(R.string.post_deleted)
        )
    }

    // launcher to start [Report Activity] and show success dialog for result
    private val reportPostLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                ReportSuccessDialog("Message").show(
                    childFragmentManager,
                    ReportSuccessDialog.TAG
                )
            }
        }
}