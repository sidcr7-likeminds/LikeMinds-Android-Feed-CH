package com.likeminds.feedsx.feed.view

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ActivityMainBinding
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseAppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        //TODO: Set as per branding
        binding.isBrandingBasic = true
        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_main_activity, intent.extras)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}