package com.likeminds.feedsx.feed.view

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.likeminds.feedsx.branding.model.BrandingData
import com.likeminds.feedsx.branding.model.Fonts
import com.likeminds.feedsx.databinding.ActivityMainBinding
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter
import com.likeminds.feedsx.posttypes.view.adapter.PostAdapter.PostAdapterListener
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity :
    AppCompatActivity(),
    PostAdapterListener {

    private var colorsList: List<String> =
        listOf("#ff0000", "#397c73", "#848659", "#ffcb00", "#bebb57")

    private var fontsList: List<Fonts> =
        listOf(
            Fonts(
                "fonts/montserrat-regular.ttf",
                "fonts/montserrat-medium.ttf",
                "fonts/montserrat-bold.ttf"
            ),
            Fonts("fonts/oswald-regular.ttf", "fonts/oswald-medium.ttf", "")
        )

    lateinit var mPostAdapter: PostAdapter
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // For testing purposes I have used 5 colors and randomly selected colors for header and button color.
        // A list of colors is passed to invalidateColors.
        // The list is of type [primaryColor, headerColor, buttonIconsColor, TextLinkColor]
        // For demonstration purpose the activity is being recreated whenever we change the color using button.
        // (so that all the views get inflated again with the new color).

        var isPrimary = (0..1).random()
        if (isPrimary == 0) {
            // in this case I have set only basic settings
            var primaryColor = colorsList[(0..4).random()]
            BrandingData.invalidateColors(listOf(primaryColor, "", "", ""))
            BrandingData.invalidateFonts(null)
            setStatusBarColor(Color.WHITE)
        } else {
            // in this case I have set only advanced settings
            var btnColor = colorsList[(0..4).random()]
            var headerColor = colorsList[(0..4).random()]
            var selectedFont = fontsList[(0..1).random()]

            BrandingData.invalidateColors(listOf("", headerColor, btnColor, "#0d9579"))
            BrandingData.invalidateFonts(selectedFont)
            setStatusBarColor(
                if (BrandingData.isBrandingBasic) Color.WHITE else Color.parseColor(
                    headerColor
                )
            )
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.isBrandingBasic = BrandingData.isBrandingBasic
        setContentView(binding.root)

        initRecyclerView()

        binding.btn.text =
            if (BrandingData.isBrandingBasic) "Basic Branding" else "Advanced Branding"

        binding.btn.setOnClickListener {
            recreateSmoothly()
        }
    }

    private fun initRecyclerView() {
        mPostAdapter = PostAdapter(this)
        binding.recyclerView.apply {
            adapter = mPostAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
                        AttachmentViewData.Builder().fileType(DOCUMENT).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(DOCUMENT).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(DOCUMENT).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(DOCUMENT).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(DOCUMENT).fileUrl("").fileSize("")
                            .build()
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
                        AttachmentViewData.Builder().fileType(IMAGE).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(VIDEO).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(IMAGE).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(IMAGE).fileUrl("").fileSize("")
                            .build(),
                        AttachmentViewData.Builder().fileType(VIDEO).fileUrl("").fileSize("")
                            .build()
                    )
                )
                .id("5")
                .user(UserViewData.Builder().name("Natesh").customTitle("Admin").build())
                .text(text)
                .build()
        )
    }

    private fun setStatusBarColor(statusBarColor: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun Activity.recreateSmoothly() {
        startActivity(Intent(this, this::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
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

    override fun onPostMenuItemClicked(postId: String, title: String) {
        Toast.makeText(this, "Post id :${postId}, Title :${title}", Toast.LENGTH_SHORT)
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