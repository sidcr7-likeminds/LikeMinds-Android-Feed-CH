package com.likeminds.feedsx.likes.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedActivityLikesBinding
import com.likeminds.feedsx.likes.model.LikesScreenExtras
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import com.likeminds.feedsx.utils.emptyExtrasException

class LMFeedLikesActivity : BaseAppCompatActivity() {

    lateinit var binding: LmFeedActivityLikesBinding

    private lateinit var likesScreenExtras: LikesScreenExtras

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val LIKES_SCREEN_EXTRAS = "LIKES_SCREEN_EXTRAS"
        const val TAG = "LMFeedLikesActivity"

        @JvmStatic
        fun start(context: Context, extras: LikesScreenExtras) {
            val intent = Intent(context, LMFeedLikesActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LIKES_SCREEN_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LikesScreenExtras): Intent {
            val intent = Intent(context, LMFeedLikesActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LIKES_SCREEN_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityLikesBinding.inflate(layoutInflater)
        binding.toolbarColor = LMFeedBranding.getToolbarColor()
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            likesScreenExtras = ExtrasUtil.getParcelable(
                bundle,
                LIKES_SCREEN_EXTRAS,
                LikesScreenExtras::class.java
            ) ?: throw emptyExtrasException(TAG)

            val args = Bundle().apply {
                putParcelable(LIKES_SCREEN_EXTRAS, likesScreenExtras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.lm_feed_nav_graph_likes, args)

            //Toolbar
            initActionBar()

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.label) {
                    LMFeedLikesFragment::class.simpleName -> {
                        binding.toolbar.setTitle(R.string.likes)
                    }
                }
            }
        } else {
            redirectActivity(true)
        }
    }

    private fun redirectActivity(isError: Boolean) {
        if (isError) {
            ViewUtils.showSomethingWentWrongToast(this)
        }
        supportFragmentManager.popBackStack()
        onBackPressedDispatcher.onBackPressed()
        overridePendingTransition(R.anim.lm_feed_slide_from_left, R.anim.lm_feed_slide_to_right)
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}