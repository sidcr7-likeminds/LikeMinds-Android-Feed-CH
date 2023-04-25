package com.likeminds.feedsampleapp.likes.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.branding.model.LMBranding
import com.likeminds.feedsampleapp.databinding.ActivityLikesBinding
import com.likeminds.feedsampleapp.likes.model.LikesScreenExtras
import com.likeminds.feedsampleapp.utils.ViewUtils
import com.likeminds.feedsampleapp.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesActivity : BaseAppCompatActivity() {

    lateinit var binding: ActivityLikesBinding

    private var likesScreenExtras: LikesScreenExtras? = null

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val LIKES_SCREEN_EXTRAS = "LIKES_SCREEN_EXTRAS"

        @JvmStatic
        fun start(context: Context, extras: LikesScreenExtras) {
            val intent = Intent(context, LikesActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LIKES_SCREEN_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LikesScreenExtras): Intent {
            val intent = Intent(context, LikesActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(LIKES_SCREEN_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLikesBinding.inflate(layoutInflater)
        binding.toolbarColor = LMBranding.getToolbarColor()
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            likesScreenExtras = bundle.getParcelable(LIKES_SCREEN_EXTRAS)
            val args = Bundle().apply {
                putParcelable(LIKES_SCREEN_EXTRAS, likesScreenExtras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph_likes, args)

            //Toolbar
            initActionBar()

            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.label) {
                    LikesFragment::class.simpleName -> {
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
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}