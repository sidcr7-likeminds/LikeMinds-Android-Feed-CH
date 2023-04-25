package com.likeminds.feedsampleapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsampleapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : com.likeminds.feedsx.utils.customview.BaseAppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        fun getIntent(
            context: Context
        ): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.toolbarColor = com.likeminds.feedsx.branding.model.LMBranding.getToolbarColor()
        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_main, intent.extras)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}