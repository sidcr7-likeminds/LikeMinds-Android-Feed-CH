package com.likeminds.feedsampleapp.notificationfeed.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.branding.model.LMBranding
import com.likeminds.feedsampleapp.databinding.ActivityNotificationFeedBinding
import com.likeminds.feedsampleapp.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFeedActivity : BaseAppCompatActivity() {

    private lateinit var binding: ActivityNotificationFeedBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, NotificationFeedActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, NotificationFeedActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationFeedBinding.inflate(layoutInflater)
        binding.toolbarColor = LMBranding.getToolbarColor()
        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_notification_feed)

        //Toolbar
        initActionBar()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.label) {
                NotificationFeedFragment::class.simpleName -> {
                    binding.toolbar.setTitle(R.string.notifications)
                }
            }
        }
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