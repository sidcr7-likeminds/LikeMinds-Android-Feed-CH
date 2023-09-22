package com.likeminds.feedsampleapp.auth.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.auth.util.AuthPreferences
import com.likeminds.feedsampleapp.databinding.ActivityAfterLoginBinding
import com.likeminds.feedsx.LikeMindsFeedUI

class AfterLoginActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authPreferences = AuthPreferences(this)
        val binding = ActivityAfterLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFeedFragment()
    }

    private fun initFeedFragment() {
        LikeMindsFeedUI.initFeed(
            this,
            R.id.frameLayout,
            authPreferences.getApiKey(),
            authPreferences.getUserName(),
            authPreferences.getUserId(),
            false
        )
    }
}