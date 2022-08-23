package com.ello.gelin.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.ello.gelin.R
import com.shop.base.ext.click
import com.shop.base.ext.lllog
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer

/**
 * @author dxl
 * @date 2022-08-23  周二
 */
class LandLayoutVideo : StandardGSYVideoPlayer {

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)


    override fun init(context: Context?) {
        super.init(context)
        findViewById<View>(R.id.btnFullScreenBack)?.click {
            if (context is Activity) {
                context.onBackPressed()
            }
        }
    }


    override fun getLayoutId(): Int {
        lllog("getLayout = $mIfCurrentIsFullscreen")
        return if (mIfCurrentIsFullscreen) {
            R.layout.video_land
        } else
            R.layout.video_normal
    }


    override fun updateStartImage() {
        if (mIfCurrentIsFullscreen) {
            if (mStartButton is ImageView) {
                val imageView = mStartButton as ImageView
                if (mCurrentState == CURRENT_STATE_PLAYING) {
                    imageView.setImageResource(R.drawable.video_click_pause_selector)
                } else if (mCurrentState == CURRENT_STATE_ERROR) {
                    imageView.setImageResource(R.drawable.video_click_play_selector)
                } else {
                    imageView.setImageResource(R.drawable.video_click_play_selector)
                }
            }
        } else {
            if (mStartButton is ImageView) {
                val imageView = mStartButton as ImageView
                if (mCurrentState == CURRENT_STATE_PLAYING) {
                    imageView.setImageResource(0)
                } else if (mCurrentState == CURRENT_STATE_ERROR) {
                    imageView.setImageResource(R.drawable.video_click_play_selector)
                } else {
                    imageView.setImageResource(R.drawable.video_click_play_selector)
                }
            }
        }
    }

//    override fun getEnlargeImageRes(): Int {
//        return R.drawable.custom_enlarge
//    }
//
//    override fun getShrinkImageRes(): Int {
//        return R.drawable.custom_shrink
//    }

    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer
    ) {
        val landLayoutVideo = gsyVideoPlayer as LandLayoutVideo
        landLayoutVideo.dismissProgressDialog()
        landLayoutVideo.dismissVolumeDialog()
        landLayoutVideo.dismissBrightnessDialog()
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
    }


    /**
     * 定义结束后的显示
     */
    override fun changeUiToCompleteClear() {
        super.changeUiToCompleteClear()
        setTextAndProgress(0, true)
        //changeUiToNormal();
    }

    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        setTextAndProgress(0, true)
        //changeUiToNormal();
    }

}