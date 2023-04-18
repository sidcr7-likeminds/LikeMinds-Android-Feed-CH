package com.likeminds.feedsx.post.edit.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.likeminds.feedsx.databinding.ActivityEditPostBinding
import com.likeminds.feedsx.post.edit.model.EditPostExtras
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
}