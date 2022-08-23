package com.ello.gelin.ui.moment.detail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.ello.gelin.R
import com.ello.gelin.databinding.ActivityMomentDetailBinding
import com.ello.gelin.ui.moment.list.Moment
import com.ello.gelin.utils.UmengUtil
import com.shop.base.common.BaseDbActivity
import com.shop.base.ext.*
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.listener.GSYVideoProgressListener
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

/**
 * @author dxl
 * @date 2022-08-17  周三
 */
class MomentDetailActivity :
    BaseDbActivity<MomentViewModel, ActivityMomentDetailBinding>(R.layout.activity_moment_detail) {
    companion object {
        fun start(context: Context, moment: Moment, name: String?) {
            context.startActivity(Intent(context, MomentDetailActivity::class.java).apply {
                putExtra("moment", moment)
                putExtra("name", name)
            })
        }
    }

    private val moment by Params<Moment>("moment")
    private val name by Params<String>("name")

    private val videoResource: Moment.Resource
        get() = moment?.resource?.first()!!

    private val orientationUtils by lazy {
        OrientationUtils(this, vb.videoPlayer)
    }


    override fun init(savedInstanceState: Bundle?) {
        vb.ivAvatar.loadImage(activity = this, url = moment?.headImage)
        vb.tvTitle.text = moment?.content?.text
        vb.tvDescription.text = "${moment?.className} | ${moment?.typeStr} | ${moment?.timeDiff}"


        //初始化不打开外部的旋转
        orientationUtils.isEnable = false

        vb.ivBlurredBg.loadImage(url = videoResource.coverImg, imageOptions = ImageOptions().apply {
            blurRadius = 200
        })
        resolveNormalVideoUI()
        with(GSYVideoOptionBuilder()) {
            setThumbImageView(ImageView(this@MomentDetailActivity).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                loadImage(url = videoResource.coverImg)
            })
            setIsTouchWiget(true)  //是否可以滑动界面改变进度，声音等
            setRotateViewAuto(true)
            setRotateWithSystem(true)
            setLockLand(true)
            setAutoFullWithSize(true)
            setShowFullAnimation(false)
            setNeedLockFull(true)
            setUrl(videoResource.url)
            setCacheWithPlay(true)
            setSurfaceErrorPlay(false)
            setVideoTitle(moment?.content?.text)
            setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    lllog("播放---------------onPrepared")
                    //开始播放了才能旋转和全屏
                    orientationUtils.isEnable = vb.videoPlayer.isRotateWithSystem
                }

                override fun onEnterFullscreen(url: String?, vararg objects: Any) {
                    super.onEnterFullscreen(url, *objects)

                }

                override fun onAutoComplete(url: String?, vararg objects: Any?) {
                    super.onAutoComplete(url, *objects)
                }

                override fun onClickStartError(url: String?, vararg objects: Any?) {
                    super.onClickStartError(url, *objects)
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)

                    // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
                    // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
                    orientationUtils.backToProtVideo()
                }
            })
                .setGSYVideoProgressListener { progress, secProgress, currentPosition, duration ->

                }
                .build(vb.videoPlayer)
        }

        vb.videoPlayer.startPlayLogic()

        vb.videoPlayer.fullscreenButton.setOnClickListener { //直接横屏
            // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
            // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
            orientationUtils.resolveByClick()

            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            vb.videoPlayer.startWindowFullscreen(this, true, true)
        }

        vb.btnFull.setOnClickListener {
            vb.videoPlayer.fullscreenButton.performClick()
        }

        vb.btnBack.setOnClickListener {
            onBackPressed()
        }

        vb.btnShare.setOnClickListener {
            moment?.let {
                UmengUtil.shareVideoToWechat(this, it.content?.text, "歌林幼儿园-${name.notNull}-${it.dateStr}", videoResource.url,videoResource.coverImg)
            }

        }


    }

    override fun onBackPressed() {
        // ------- ！！！如果不需要旋转屏幕，可以不调用！！！-------
        // 不需要屏幕旋转，还需要设置 setNeedOrientationUtils(false)
        orientationUtils.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }

    private fun getCurPlay(): GSYVideoPlayer {
        return if (vb.videoPlayer.fullWindowPlayer != null) {
            vb.videoPlayer.fullWindowPlayer
        } else vb.videoPlayer
    }

    override fun onPause() {
        getCurPlay().onVideoPause()
        super.onPause()
    }

    override fun onResume() {
        getCurPlay().onVideoResume(false)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        getCurPlay().release()
        //GSYPreViewManager.instance().releaseMediaPlayer();
        orientationUtils.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        vb.videoPlayer.onConfigurationChanged(
            this,
            newConfig,
            orientationUtils,
            true,
            true
        )
    }

    private fun resolveNormalVideoUI() {
        //增加title
        vb.videoPlayer.titleTextView.visibility = View.GONE
        vb.videoPlayer.backButton.visibility = View.GONE
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_bottom_out)
    }


}