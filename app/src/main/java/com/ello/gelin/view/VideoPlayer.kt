package com.ello.gelin.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import com.ello.gelin.R
import com.shop.base.ext.ImageOptions
import com.shop.base.ext.loadImage
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView

/**
 * @author dxl
 * @date 2022-08-17  周三
 */
class VideoPlayer  @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    StandardGSYVideoPlayer(context, attrs) {

    var start: ImageView? = null

    private val ivCover by lazy { findViewById<ImageView>(R.id.ivCover) }

    override fun getLayoutId() = R.layout.video_player

    override fun init(context: Context?) {
        super.init(context)
        start = findViewById(R.id.start)
        isShowFullAnimation = false
        isNeedLockFull = true
        isLockLand = true
        findViewById<View>(R.id.fullscreen).setOnClickListener {
            startWindowFullscreen(context, false, true);
        }
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
        //不给触摸快进，如果需要，屏蔽下方代码即可
        mChangePosition = false

        //不给触摸音量，如果需要，屏蔽下方代码即可
        mChangeVolume = false

        //不给触摸亮度，如果需要，屏蔽下方代码即可
        mBrightness = false
    }

    fun setUpLazy(url: String?, cover: String?) {
        setUpLazy(url, true, context.externalCacheDir, null, "")
        ivCover.loadImage(context = context, url = cover, imageOptions = ImageOptions())
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> imageView.setImageResource(R.drawable.ic_pause_white_24dp)
                GSYVideoView.CURRENT_STATE_ERROR -> imageView.setImageResource(R.drawable.ic_play_white_24dp)
                else -> imageView.setImageResource(R.drawable.ic_play_white_24dp)
            }
        } else {
            super.updateStartImage()
        }
    }




    override fun touchDoubleUp(e: MotionEvent?) {
        super.touchDoubleUp(e)
        //不需要双击暂停
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
//        mBottomContainer.isVisible = false
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
//        mBottomContainer.isVisible = false
    }

    //播放中
    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
//        mBottomContainer.isVisible = false
//        start?.isVisible = false
    }

    //开始缓冲
    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
    }

    //暂停
    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
//        mBottomContainer.isVisible = false
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
    }

    //错误状态
    override fun changeUiToError() {
        super.changeUiToError()
    }

    override fun onClickUiToggle(e: MotionEvent?) {
        super.onClickUiToggle(e)
    }

    init {
        mVideoAllCallBack = object : VideoViewCallback() {

            override fun onPrepared(url: String, vararg objects: Any) {
                super.onPrepared(url, *objects)
                //准备完成隐藏封面
                ivCover.isVisible = false
            }

            override fun onStartPrepared(url: String, vararg objects: Any) {
                super.onStartPrepared(url, *objects)
                //开始加载时显示封面
                ivCover.isVisible = true
            }


        }

    }

}