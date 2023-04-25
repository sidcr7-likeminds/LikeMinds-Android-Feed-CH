package com.likeminds.feedsampleapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsampleapp.databinding.ActivityMainBinding
import com.likeminds.feedsx.feed.model.FeedExtras
import com.likeminds.feedsx.feed.view.FeedFragment
import com.likeminds.feedsx.utils.ViewUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : com.likeminds.feedsx.utils.customview.BaseAppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var feedExtras: FeedExtras? = null

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        @JvmStatic
        fun getIntent(context: Context, extras: FeedExtras): Intent {
            val intent = Intent(context, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(FeedFragment.FEED_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            feedExtras = bundle.getParcelable(FeedFragment.FEED_EXTRAS)
            val args = Bundle().apply {
                putParcelable(FeedFragment.FEED_EXTRAS, feedExtras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph_main, args)
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
        overridePendingTransition(
            com.likeminds.feedsx.R.anim.slide_from_left,
            com.likeminds.feedsx.R.anim.slide_to_right
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}