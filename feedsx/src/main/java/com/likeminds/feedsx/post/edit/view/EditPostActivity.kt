package com.likeminds.feedsx.post.edit.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.R
import com.likeminds.feedsx.databinding.ActivityEditPostBinding
import com.likeminds.feedsx.post.edit.model.EditPostExtras
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity

class EditPostActivity : BaseAppCompatActivity() {

    lateinit var binding: ActivityEditPostBinding

    private var editPostExtras: EditPostExtras? = null

    //Navigation
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    companion object {
        const val EDIT_POST_EXTRAS = "EDIT_POST_EXTRAS"

        @JvmStatic
        fun start(context: Context, extras: EditPostExtras) {
            val intent = Intent(context, EditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(EDIT_POST_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            context.startActivity(intent)
        }

        @JvmStatic
        fun getIntent(context: Context, extras: EditPostExtras): Intent {
            val intent = Intent(context, EditPostActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(EDIT_POST_EXTRAS, extras)
            intent.putExtra("bundle", bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.getBundleExtra("bundle")

        if (bundle != null) {
            editPostExtras = bundle.getParcelable(EDIT_POST_EXTRAS)
            val args = Bundle().apply {
                putParcelable(EDIT_POST_EXTRAS, editPostExtras)
            }

            //Navigation
            navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController = navHostFragment.navController
            navController.setGraph(R.navigation.nav_graph_edit_post, args)
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
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}