package com.likeminds.feedsx.post.create.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.LmFeedActivitySelectAuthorBinding
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class LMFeedSelectAuthorActivity : BaseAppCompatActivity() {

    lateinit var binding: LmFeedActivitySelectAuthorBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, LMFeedSelectAuthorActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, LMFeedSelectAuthorActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivitySelectAuthorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.lm_feed_nav_graph_select_author)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}