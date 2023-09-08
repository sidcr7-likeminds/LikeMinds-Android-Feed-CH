package com.likeminds.feedsx.notificationfeed.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedActivityNotificationFeedBinding
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class LMFeedNotificationFeedActivity : BaseAppCompatActivity() {

    private lateinit var binding: LmFeedActivityNotificationFeedBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, LMFeedNotificationFeedActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, LMFeedNotificationFeedActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LmFeedActivityNotificationFeedBinding.inflate(layoutInflater)
        binding.toolbarColor = LMFeedBranding.getToolbarColor()
        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.lm_feed_nav_graph_notification_feed)

        //Toolbar
        initActionBar()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.label) {
                LMFeedNotificationFeedFragment::class.simpleName -> {
                    binding.toolbar.setTitle(R.string.notifications)
                }
            }
        }
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