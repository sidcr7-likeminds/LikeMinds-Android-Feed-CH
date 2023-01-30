package com.likeminds.feedsx.feed.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ActivityLikesBinding
import com.likeminds.feedsx.feed.view.model.LikesScreenExtras
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
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
        //TODO: Set as per branding
        binding.isBrandingBasic = true
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
        //TODO Change error message.
        if (isError) {
            ViewUtils.showShortToast(this, getString(R.string.request_not_processed))
        }
        supportFragmentManager.popBackStack()
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolbar)
        val likesCount = likesScreenExtras!!.likesCount
        binding.tvToolbarSubTitle.text =
            this.resources.getQuantityString(
                R.plurals.likes_small,
                likesCount,
                likesCount
            )
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}