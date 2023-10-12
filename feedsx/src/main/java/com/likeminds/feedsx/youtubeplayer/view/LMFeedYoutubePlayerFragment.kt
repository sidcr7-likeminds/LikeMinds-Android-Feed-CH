package com.likeminds.feedsx.youtubeplayer.view

import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedFragmentYoutubePlayerBinding
import com.likeminds.feedsx.posttypes.viewmodel.LMFeedYoutubePlayerViewModel
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.ViewUtils.hide
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.emptyExtrasException
import com.likeminds.feedsx.youtubeplayer.model.LMFeedYoutubePlayerExtras
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener


class LMFeedYoutubePlayerFragment :
    BaseFragment<LmFeedFragmentYoutubePlayerBinding, LMFeedYoutubePlayerViewModel>() {

    private lateinit var youtubePlayerExtras: LMFeedYoutubePlayerExtras

    companion object {
        const val TAG = "LMFeedYoutubePlayerFragment"
    }

    override fun getViewModelClass(): Class<LMFeedYoutubePlayerViewModel> {
        return LMFeedYoutubePlayerViewModel::class.java
    }

    override fun getViewBinding(): LmFeedFragmentYoutubePlayerBinding {
        return LmFeedFragmentYoutubePlayerBinding.inflate(layoutInflater)
    }

    override fun attachDagger() {
        super.attachDagger()
        SDKApplication.getInstance().lmFeedYoutubePlayerComponent()?.inject(this)
    }

    override fun receiveExtras() {
        super.receiveExtras()
        if (arguments == null || arguments?.containsKey(LMFeedYoutubePlayerActivity.YOUTUBE_PLAYER_EXTRAS) == null) {
            requireActivity().supportFragmentManager.popBackStack()
            return
        }
        youtubePlayerExtras = ExtrasUtil.getParcelable(
            arguments,
            LMFeedYoutubePlayerActivity.YOUTUBE_PLAYER_EXTRAS,
            LMFeedYoutubePlayerExtras::class.java
        ) ?: throw emptyExtrasException(TAG)
    }

    override fun setUpViews() {
        super.setUpViews()
        setupYoutubePlayer()
    }

    // setups tge youtube player
    private fun setupYoutubePlayer() {
        binding.apply {
            // adds [youtubePlayerView] as a lifecycle observer of the fragment
            lifecycle.addObserver(youtubePlayerView)

            // adds listener for youtube
            youtubePlayerView.addYouTubePlayerListener(
                object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        super.onReady(youTubePlayer)

                        viewYoutubePlayerBackground.hide()
                        pbLoadingYoutube.hide()
                        youTubePlayer.loadVideo(youtubePlayerExtras.videoId, 0F)
                    }
                })
        }
    }
}