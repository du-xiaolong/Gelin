package com.ello.gelin.ui.moment.detail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.ello.gelin.R
import com.ello.gelin.databinding.ActivityMomentDetailBinding
import com.ello.gelin.ui.moment.list.Moment
import com.shop.base.common.BaseDbActivity
import com.shop.base.ext.*
import com.shop.base.util.delayDo
import com.shuyu.gsyvideoplayer.GSYVideoADManager
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 * @author dxl
 * @date 2022-08-17  周三
 */
class MomentDetailActivity :
    BaseDbActivity<MomentViewModel, ActivityMomentDetailBinding>(R.layout.activity_moment_detail) {
    companion object {
        fun start(context: Context, moment: Moment) {
            context.startActivity(Intent(context, MomentDetailActivity::class.java).apply {
                putExtra("moment", moment)
            })
        }
    }

    private val moment by Params<Moment>("moment")

    private var orientationUtils: OrientationUtils? = null


    override fun init(savedInstanceState: Bundle?) {
        setupViews()
    }

    override fun onPause() {
        super.onPause()
        vb.videoPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        vb.videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoADManager.releaseAllVideos()
        orientationUtils?.releaseListener()
        vb.videoPlayer.release()
        vb.videoPlayer.setVideoAllCallBack(null)
    }

    override fun onBackPressed() {
        orientationUtils?.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) return
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_bottom_out)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        vb.videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
    }

    fun setupViews() {
        orientationUtils = OrientationUtils(this, vb.videoPlayer)
        startVideoPlayer()
    }


    private fun startVideoPlayer() {
        vb.ivBlurredBg.loadImage(
            activity = this,
            url = moment?.resource?.firstOrNull()?.coverImg,
            imageOptions = getDefaultImageOptions().apply {
                blurRadius = 150
            })
        vb.videoPlayer.startPlay()
    }

    private fun GSYVideoPlayer.startPlay() {
        //设置全屏按键功能,这是使用的是选择屏幕，而不是全屏
        fullscreenButton.setOnClickListener { showFull() }
        //防止错位设置
        playTag = this.javaClass.simpleName
        //音频焦点冲突时是否释放
        isReleaseWhenLossAudio = false
        //增加封面
        val imageView = ImageView(this@MomentDetailActivity)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.loadImage(url = moment?.resource?.firstOrNull()?.coverImg)
        thumbImageView = imageView
        thumbImageView.setOnClickListener { switchTitleBarVisible() }
        //是否开启自动旋转
        isRotateViewAuto = false
        //是否需要全屏锁定屏幕功能
        isNeedLockFull = true
        //是否可以滑动调整
        setIsTouchWiget(true)
        //设置触摸显示控制ui的消失时间
        dismissControlTime = 5000
        //设置播放过程中的回调
        setVideoAllCallBack(VideoCallPlayBack())
        //设置播放URL
        setUp(moment?.resource?.firstOrNull()?.url, false, "")
        //开始播放
        startPlayLogic()
    }

    private fun showFull() {
        orientationUtils?.resolveByClick()
    }

    private fun switchTitleBarVisible() {
        if (vb.videoPlayer.currentPlayer.currentState == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE) return
        if (vb.flHeader.visibility == View.VISIBLE) {
            hideTitleBar()
        } else {
            vb.flHeader.visibleAlphaAnimation(1000)
            vb.ivPullDown.visibleAlphaAnimation(1000)
            vb.ivCollection.visibleAlphaAnimation(1000)
            vb.ivMore.visibleAlphaAnimation(1000)
            vb.ivShare.visibleAlphaAnimation(1000)
            delayHideTitleBar()
        }
    }

    private fun hideTitleBar() {
        vb.flHeader.invisibleAlphaAnimation(1000)
        vb.ivPullDown.goneAlphaAnimation(1000)
        vb.ivCollection.goneAlphaAnimation(1000)
        vb.ivMore.goneAlphaAnimation(1000)
        vb.ivShare.goneAlphaAnimation(1000)
    }

    private fun delayHideTitleBar() {
        delayDo(2000) {
            hideTitleBar()
        }
    }

    private fun delayHideBottomContainer() {
        delayDo(2000) {
            vb.videoPlayer.getBottomContainer().isVisible = false
            vb.videoPlayer.startButton.isVisible = false
        }

    }

    inner class VideoCallPlayBack : GSYSampleCallBack() {
        override fun onStartPrepared(url: String?, vararg objects: Any?) {
            super.onStartPrepared(url, *objects)
            vb.flHeader.isVisible = false
        }

        override fun onClickBlank(url: String?, vararg objects: Any?) {
            super.onClickBlank(url, *objects)
            switchTitleBarVisible()
        }

        override fun onClickStop(url: String?, vararg objects: Any?) {
            super.onClickStop(url, *objects)
            delayHideBottomContainer()
        }

        override fun onAutoComplete(url: String?, vararg objects: Any?) {
            super.onAutoComplete(url, *objects)
            vb.flHeader.isVisible = true
            vb.ivPullDown.isVisible = true
            vb.ivCollection.isVisible = false
            vb.ivShare.isVisible = false
            vb.ivMore.isVisible = false
        }
    }

}