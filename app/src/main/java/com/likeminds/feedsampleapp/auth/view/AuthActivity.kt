package com.likeminds.feedsampleapp.auth.view

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import com.likeminds.feedsampleapp.R
import com.likeminds.feedsampleapp.auth.util.AuthPreferences
import com.likeminds.feedsampleapp.databinding.ActivityAuthBinding
import com.likeminds.feedsx.LikeMindsFeedUI
import com.likeminds.feedsx.branding.model.LMFeedFonts
import com.likeminds.feedsx.branding.model.SetFeedBrandingRequest
import com.likeminds.feedsx.utils.Route
import javax.inject.Singleton

@Singleton
class AuthActivity : AppCompatActivity() {

    private lateinit var authPreferences: AuthPreferences

    private lateinit var binding: ActivityAuthBinding

    private var headerColor = DEFAULT_HEADER_COLOR
    private var buttonColor = DEFAULT_BUTTON_COLOR
    private var textLinkColor = DEFAULT_TEXT_LINK

    companion object {
        const val DEFAULT_HEADER_COLOR = "#FFFFFF"
        const val DEFAULT_BUTTON_COLOR = "#6200EE"
        const val DEFAULT_TEXT_LINK = "#007AFF"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authPreferences = AuthPreferences(this)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isLoggedIn = authPreferences.getIsLoggedIn()

        if (isLoggedIn) {
            // user already logged in, navigate using deep linking or to [MainActivity]
            if (intent.data != null) {
                parseDeepLink()
            } else {
                navigateToAfterLogin()
            }
        } else {
            // user is not logged in, ask login details.
            loginUser()
        }
    }

    // parses deep link to start corresponding activity
    private fun parseDeepLink() {
        //get intent for route
        val intent = Route.handleDeepLink(
            this,
            intent.data.toString()
        )
        startActivity(intent)
        finish()
    }

    // navigates user to [AfterLoginActivity]
    private fun navigateToAfterLogin() {
        val intent = Intent(this, AfterLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // validates user input and save login details
    private fun loginUser() {
        binding.apply {
            val context = root.context

            ivBrandingButton.setOnClickListener {
                showColorDialog { colorRes, colorHex ->
                    buttonColor = colorHex
                    ivBrandingButton.backgroundTintList = ColorStateList.valueOf(colorRes)
                }
            }

            ivBrandingHeader.setOnClickListener {
                showColorDialog { colorRes, colorHex ->
                    headerColor = colorHex
                    ivBrandingHeader.backgroundTintList = ColorStateList.valueOf(colorRes)
                }
            }

            ivBrandingTextLink.setOnClickListener {
                showColorDialog { colorRes, colorHex ->
                    textLinkColor = colorHex
                    ivBrandingTextLink.backgroundTintList = ColorStateList.valueOf(colorRes)
                }
            }


            btnLogin.setOnClickListener {
                val apiKey = binding.etApiKey.text.toString().trim()
                val userName = binding.etUserName.text.toString().trim()
                val userId = binding.etUserId.text.toString().trim()

                if (apiKey.isEmpty()) {
                    com.likeminds.feedsx.utils.ViewUtils.showShortToast(
                        context,
                        getString(R.string.enter_api_key)
                    )
                    return@setOnClickListener
                }

                if (userName.isEmpty()) {
                    com.likeminds.feedsx.utils.ViewUtils.showShortToast(
                        context,
                        getString(R.string.enter_user_name)
                    )
                    return@setOnClickListener
                }

                if (userId.isEmpty()) {
                    com.likeminds.feedsx.utils.ViewUtils.showShortToast(
                        context,
                        getString(R.string.enter_user_id)
                    )
                    return@setOnClickListener
                }

                // save login details to auth prefs
                authPreferences.saveIsLoggedIn(true)
                authPreferences.saveApiKey(apiKey)
                authPreferences.saveUserName(userName)
                authPreferences.saveUserId(userId)

                authPreferences.saveHeaderColor(headerColor)
                authPreferences.saveButtonColor(buttonColor)
                authPreferences.saveTextLinkColor(textLinkColor)

                val brandingRequest = SetFeedBrandingRequest.Builder()
                    .headerColor(headerColor)
                    .buttonsColor(buttonColor)
                    .textLinkColor(textLinkColor)
                    .fonts(
                        LMFeedFonts.Builder()
                            .bold("fonts/montserrat-bold.ttf")
                            .medium("fonts/montserrat-medium.ttf")
                            .regular("fonts/montserrat-regular.ttf")
                            .build()
                    )
                    .build()

                LikeMindsFeedUI.setBranding(brandingRequest)

                navigateToAfterLogin()
            }
        }
    }

    private fun showColorDialog(cb: (Int, String) -> Unit) {
        MaterialColorPickerDialog
            .Builder(this)
            .setTitle("Pick Theme")
            .setColorShape(ColorShape.SQAURE)
            .setColorSwatch(ColorSwatch._300)
            .setColorListener { colorRes, colorHex ->
                cb(colorRes, colorHex)
            }
            .show()
    }
}