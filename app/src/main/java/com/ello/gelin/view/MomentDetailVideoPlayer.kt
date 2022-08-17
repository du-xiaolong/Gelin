package com.ello.gelin.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.isVisible
import com.ello.gelin.R
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 * @author dxl
 * @date 2022-08-17  周三
 */
class MomentDetailVideoPlayer @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    StandardGSYVideoPlayer(context, attrs) {

    override fun getLayoutId() = R.layout.video_player_moment_detail

    /**
     *  是否第一次加载视频。用于隐藏进度条、播放按钮等UI。播放完成后，重新加载视频，会重置为true。
     */
    private var initFirstLoad = true

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> {
                    imageView.setImageResource(R.drawable.ic_pause_white_24dp)
                    imageView.setBackgroundResource(R.drawable.sel_pause_white_bg)
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24dp)
                    imageView.setBackgroundResource(R.drawable.sel_play_white_bg)
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageResource(R.drawable.ic_refresh_white_24dp)
                    imageView.setBackgroundResource(0)
                }
                else -> {
                    imageView.setImageResource(R.drawable.ic_play_white_24dp)
                    imageView.setBackgroundResource(R.drawable.sel_play_white_bg)
                }
            }

        } else {
            super.updateStartImage()
        }
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        initFirstLoad = true
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        mBottomContainer.isVisible = false
        mStartButton.isVisible = false
    }

    //播放中
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        if (initFirstLoad) {
            mBottomContainer.isVisible = false
            mStartButton.isVisible = false
        }
        initFirstLoad = false
    }

    //开始缓冲
    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
    }

    //暂停
    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        mBottomContainer.isVisible = false
    }

    //错误状态
    override fun changeUiToError() {
        super.changeUiToError()
    }

    fun getBottomContainer() = mBottomContainer
    
}