package com.likeminds.feedsx.youtubeplayer.view

import com.likeminds.feedsx.SDKApplication
import com.likeminds.feedsx.databinding.LmFeedFragmentYoutubePlayerBinding
import com.likeminds.feedsx.utils.ExtrasUtil
import com.likeminds.feedsx.utils.customview.BaseFragment
import com.likeminds.feedsx.utils.emptyExtrasException
import com.likeminds.feedsx.youtubeplayer.model.LMFeedYoutubePlayerExtras

class LMFeedYoutubePlayerFragment : BaseFragment<LmFeedFragmentYoutubePlayerBinding, Nothing>() {

    private lateinit var youtubePlayerExtras: LMFeedYoutubePlayerExtras

    companion object {
        const val TAG = "LMFeedYoutubePlayerFragment"
    }

    override fun getViewModelClass(): Class<Nothing>? {
        return null
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
}