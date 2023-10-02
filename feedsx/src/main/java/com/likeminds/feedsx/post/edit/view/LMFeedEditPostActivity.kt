package com.likeminds.feedsx.post.edit.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedActivityEditPostBinding
import com.likeminds.feedsx.post.create.view.LMFeedCreatePostFragment
import com.likeminds.feedsx.post.edit.model.LMFeedEditPostExtras
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.ViewUtils.currentFragment
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class LMFeedEditPostActivity : BaseAppCompatActivity() {

    lateinit var binding: LmFeedActivityEditPostBinding

    private var editPostExtras: LMFeedEditPostExtras? = null

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val EDIT_POST_EXTRAS = "EDIT_POST_EXTRAS"

        @JvmStatic
        fun start(context: Context, extras: LMFeedEditPostExtras) {
            val intent = Intent(context, LMFeedEditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(EDIT_POST_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedEditPostExtras): Intent {
            val intent = Intent(context, LMFeedEditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(EDIT_POST_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOnBackPressedCallback()

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            editPostExtras = ExtrasUtil.getParcelable(
                bundle,
                EDIT_POST_EXTRAS,
                LMFeedEditPostExtras::class.java
            )

            val args = Bundle().apply {
                putParcelable(EDIT_POST_EXTRAS, editPostExtras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.lm_feed_nav_graph_edit_post, args)
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

    // setups up on back pressed callback
    private fun setupOnBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.currentFragment(R.id.nav_host_fragment) is EditPostFragment) {
                val fragment =
                    supportFragmentManager.currentFragment(R.id.nav_host_fragment) as EditPostFragment
                fragment.openBackPressedPopup()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}