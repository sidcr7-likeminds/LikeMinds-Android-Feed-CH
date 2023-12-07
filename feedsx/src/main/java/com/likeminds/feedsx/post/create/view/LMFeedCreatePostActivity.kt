package com.likeminds.feedsx.post.create.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedActivityCreatePostBinding
import com.likeminds.feedsx.post.create.model.LMFeedCreatePostExtras
import com.likeminds.feedsx.posttypes.model.*
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.ViewUtils.currentFragment
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class LMFeedCreatePostActivity : BaseAppCompatActivity() {

    lateinit var binding: LmFeedActivityCreatePostBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val CREATE_POST_EXTRAS = "CREATE_POST_EXTRAS"
        const val RESULT_UPLOAD_POST = Activity.RESULT_FIRST_USER + 1

        @JvmStatic
        fun start(context: Context, extras: LMFeedCreatePostExtras) {
            val intent = Intent(context, LMFeedCreatePostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CREATE_POST_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedCreatePostExtras): Intent {
            val intent = Intent(context, LMFeedCreatePostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(CREATE_POST_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LmFeedActivityCreatePostBinding.inflate(layoutInflater)
        binding.toolbarColor = LMFeedBranding.getToolbarColor()
        setContentView(binding.root)

        setupOnBackPressedCallback()

        val createPostExtras = ExtrasUtil.getParcelable(
            intent.getBundleExtra("bundle"),
            CREATE_POST_EXTRAS,
            LMFeedCreatePostExtras::class.java
        )

        val args = Bundle().apply {
            putParcelable(CREATE_POST_EXTRAS, createPostExtras)
        }

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.lm_feed_nav_graph_create_post, args)

        //Toolbar
        setSupportActionBar(binding.toolbar)
        binding.ivBack.setOnClickListener {
            val fragment = supportFragmentManager.currentFragment(R.id.nav_host_fragment)
            if (fragment is LMFeedCreatePostFragment) {
                fragment.openBackPressedPopup()
            } else {
                supportFragmentManager.popBackStack()
            }
        }

        binding.tvToolbarTitle.apply {
            when (createPostExtras?.attachmentType) {
                VIDEO -> {
                    text = getString(R.string.add_video_resource)
                }

                DOCUMENT -> {
                    text = getString(R.string.add_pdf_resource)
                }

                LINK -> {
                    text = getString(R.string.add_link_resource)
                }

                ARTICLE -> {
                    text = getString(R.string.add_article)
                }

                else -> {
                    text = getString(R.string.create_a_s)
                }
            }
        }
    }

    // setups up on back pressed callback
    private fun setupOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.currentFragment(R.id.nav_host_fragment) is LMFeedCreatePostFragment) {
                val fragment =
                    supportFragmentManager.currentFragment(R.id.nav_host_fragment) as LMFeedCreatePostFragment
                fragment.openBackPressedPopup()
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}