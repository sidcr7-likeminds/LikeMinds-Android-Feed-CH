package com.likeminds.feedsx.post.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ActivityCreatePostBinding
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostActivity : BaseAppCompatActivity() {

    private lateinit var binding: ActivityCreatePostBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {

        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, CreatePostActivity::class.java))
        }

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, CreatePostActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)

        //TODO: Set as per branding
        binding.isBrandingBasic = true

        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_create_post, intent.extras)

        //Toolbar
        setSupportActionBar(binding.toolbar)
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.label) {
                CreatePostFragment::class.simpleName -> {
                    binding.toolbar.setTitle(R.string.create_a_post)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

}