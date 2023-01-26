package com.likeminds.feedsx.feed.view

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ActivityLikesBinding
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LikesActivity : BaseAppCompatActivity() {

    lateinit var binding: ActivityLikesBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_likes)

        binding = ActivityLikesBinding.inflate(layoutInflater)
        //TODO: Set as per branding
        binding.isBrandingBasic = true
        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_likes, intent.extras)

        //Toolbar
        setSupportActionBar(binding.toolbar)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.label) {
                LikesFragment::class.simpleName -> {
                    binding.toolbar.setTitle(R.string.likes)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}