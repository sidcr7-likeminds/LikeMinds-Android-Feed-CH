package com.likeminds.feedsx.topic.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.branding.model.LMFeedBranding
import com.likeminds.feedsx.databinding.LmFeedActivityTopicSelectionBinding
import com.likeminds.feedsx.topic.model.LMFeedTopicSelectionExtras
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import com.likeminds.feedsx.utils.emptyExtrasException

class LMFeedTopicSelectionActivity : BaseAppCompatActivity() {

    lateinit var binding: LmFeedActivityTopicSelectionBinding

    private lateinit var extras: LMFeedTopicSelectionExtras

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val TAG = "LMFeedTopicSelectionActivity"
        const val TOPIC_SELECTION_EXTRAS = "TOPIC_SELECTION_EXTRAS"

        @JvmStatic
        fun start(context: Context, extras: LMFeedTopicSelectionExtras) {
            val intent = Intent(context, LMFeedTopicSelectionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(TOPIC_SELECTION_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: LMFeedTopicSelectionExtras): Intent {
            val intent = Intent(context, LMFeedTopicSelectionActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(TOPIC_SELECTION_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().lmFeedTopicComponent()?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = LmFeedActivityTopicSelectionBinding.inflate(layoutInflater)
        binding.toolbarColor = LMFeedBranding.getToolbarColor()
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        bundle?.let {
            extras = ExtrasUtil.getParcelable(
                it,
                TOPIC_SELECTION_EXTRAS,
                LMFeedTopicSelectionExtras::class.java
            ) ?: throw emptyExtrasException(TAG)

            val args = Bundle().apply {
                putParcelable(TOPIC_SELECTION_EXTRAS, extras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.lm_feed_nav_graph_topic_selection, args)

            //Toolbar
            initActionBar()

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.label) {
                    LMFeedTopicSelectionFragment::class.simpleName -> {
                        binding.tvToolbarTitle.text = getString(R.string.select_topic)
                    }
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
}