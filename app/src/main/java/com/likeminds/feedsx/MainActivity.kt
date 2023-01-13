package com.likeminds.feedsx

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.likeminds.feedsx.databinding.ActivityMainBinding
import com.likeminds.feedsx.utils.branding.BrandingData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var colorsList: List<String> =
        listOf("#ff0000", "#397c73", "#848659", "#ffcb00", "#bebb57")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // For testing purposes I have used 5 colors and randomly selected colors for header and button color.
        // A list of colors is passed to invalidateColors.
        // The list is of type [primaryColor, headerColor, buttonIconsColor, TextLinkColor]
        // For demonstration purpose the activity is being recreated whenever we change the color using button.
        // (so that all the views get inflated again with the new color).

        var isPrimary = (0..1).random()
        if (isPrimary == 0) {
            // in this case I have set only basic settings
            var primaryColor = colorsList[(0..4).random()]
            BrandingData.invalidateColors(listOf(primaryColor, "", "", ""))
            setStatusBarColor(Color.WHITE)
        } else {
            // in this case I have set only advanced settings
            var btnColor = colorsList[(0..4).random()]
            var headerColor = colorsList[(0..4).random()]
            BrandingData.invalidateColors(listOf("", headerColor, btnColor, "#0d9579"))
            setStatusBarColor(
                if (BrandingData.isBrandingBasic) Color.WHITE else Color.parseColor(
                    headerColor
                )
            )
        }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        binding.isBrandingBasic = BrandingData.isBrandingBasic
        setContentView(binding.root)

        if (BrandingData.isBrandingBasic) {
            binding.btn.text =
                if (BrandingData.isBrandingBasic) "Basic Branding" else "Advanced Branding"
        }

        binding.btn.setOnClickListener {
            recreateSmoothly()
        }
    }

    private fun setStatusBarColor(statusBarColor: Int) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = statusBarColor
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun Activity.recreateSmoothly() {
        startActivity(Intent(this, this::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

}