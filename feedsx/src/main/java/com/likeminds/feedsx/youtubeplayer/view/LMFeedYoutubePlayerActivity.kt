package com.likeminds.feedsx.youtubeplayer.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedActivityYoutubePlayerBinding
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import com.likeminds.feedsx.youtubeplayer.model.LMFeedYoutubePlayerExtras

class LMFeedYoutubePlayerActivity : BaseAppCompatActivity() {

    lateinit var binding: LmFeedActivityYoutubePlayerBinding

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val YOUTUBE_PLAYER_EXTRAS = "YOUTUBE_PLAYER_EXTRAS"

        @JvmStatic
        fun start(context: Context, extras: LMFeedYoutubePlayerExtras) {
            val intent = Intent(context, LMFeedYoutubePlayerActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(YOUTUBE_PLAYER_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedYoutubePlayerExtras): Intent {
            val intent = Intent(context, LMFeedYoutubePlayerExtras::class.java)
            val bundle = Bundle()
            bundle.putParcelable(YOUTUBE_PLAYER_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().lmFeedYoutubePlayerComponent()?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LmFeedActivityYoutubePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val youtubePlayerExtras = ExtrasUtil.getParcelable(
            intent.getBundleExtra("bundle"),
            YOUTUBE_PLAYER_EXTRAS,
            LMFeedYoutubePlayerExtras::class.java
        )

        val args = Bundle().apply {
            putParcelable(YOUTUBE_PLAYER_EXTRAS, youtubePlayerExtras)
        }

        //Navigation
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.setGraph(R.navigation.lm_feed_nav_graph_youtube_player, args)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}