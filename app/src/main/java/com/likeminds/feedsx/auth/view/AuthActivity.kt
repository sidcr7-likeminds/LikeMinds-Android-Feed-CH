package com.likeminds.feedsx.auth.view

import android.os.Bundle
import com.likeminds.feedsx.R
import com.likeminds.feedsx.auth.util.AuthPreferences
import com.likeminds.feedsx.databinding.ActivityAuthBinding
import com.likeminds.feedsx.feed.view.MainActivity
import com.likeminds.feedsx.utils.ViewUtils
import com.likeminds.feedsx.utils.customview.BaseAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseAppCompatActivity() {

    @Inject
    lateinit var authPreferences: AuthPreferences

    lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = authPreferences.getIsLoggedIn()

        if (isLoggedIn) {
            // user already logged in, navigate to [MainActivity]
            navigateToMain()
        } else {
            // user is not logged in, ask login details.
            loginUser()
        }
    }

    // navigates user to [MainActivity]
    private fun navigateToMain() {
        val intent = MainActivity.getIntent(this)
        startActivity(intent)
        finish()
    }

    // validates user input and save login details
    private fun loginUser() {
        binding.apply {
            val context = this.root.context

            btnLogin.setOnClickListener {
                val apiKey = binding.etApiKey.text.toString().trim()
                val userName = binding.etUserName.text.toString().trim()
                val userId = binding.etUserId.text.toString().trim()

                if (apiKey.isEmpty()) {
                    ViewUtils.showShortToast(context, getString(R.string.enter_api_key))
                    return@setOnClickListener
                }

                if (userName.isEmpty()) {
                    ViewUtils.showShortToast(context, getString(R.string.enter_user_name))
                    return@setOnClickListener
                }

                if (userId.isEmpty()) {
                    ViewUtils.showShortToast(context, getString(R.string.enter_user_id))
                    return@setOnClickListener
                }

                // save login details to auth prefs
                authPreferences.saveIsLoggedIn(true)
                authPreferences.saveApiKey(apiKey)
                authPreferences.saveUserName(userName)
                authPreferences.saveUserId(userId)
                navigateToMain()
            }
        }
    }
}