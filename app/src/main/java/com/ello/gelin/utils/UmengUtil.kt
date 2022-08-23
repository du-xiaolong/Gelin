package com.ello.gelin.utils

import android.app.Activity
import android.content.Context
import com.blankj.utilcode.util.LogUtils
import com.ello.gelin.BuildConfig
import com.shop.base.ext.toast
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMVideo
import com.umeng.socialize.media.UMWeb

/**
 * @author dxl
 * @date 2022-08-23  周二
 */
object UmengUtil {

    private const val UMENG_APP_KEY = "616f7538e0f9bb492b342942"
    private const val WX_APP_KEY = "wxa5d240164296b1ed"
    private const val WX_APP_SECRET = "db8eb8c858a5982389103c8636291b68"

    fun init(appContext: Context) {

        UMConfigure.init(appContext, UMENG_APP_KEY, null, UMConfigure.DEVICE_TYPE_PHONE, "")

        //微信
        PlatformConfig.setWeixin(WX_APP_KEY, WX_APP_SECRET)
        PlatformConfig.setWXFileProvider("${BuildConfig.APPLICATION_ID}.fileProvider")

    }


    /**
     * 分享纯文本到微信
     */
    fun shareTextToWechat(activity: Activity, text:String) {
        ShareAction(activity)
            .setPlatform(SHARE_MEDIA.WEIXIN)
            .withText(text)
            .share()
    }

    /**
     * 检查应用是否安装
     */
    @Suppress("SameParameterValue")
    private fun checkInstall(activity: Activity, platform: SHARE_MEDIA): Boolean {
        if (!UMShareAPI.get(activity).isInstall(activity, platform)) {
            return false
        }
        return true
    }

    /**
     * 分享网页到微信
     *
     */
    fun shareWebToWechat(
        activity: Activity,
        url: String?,
        title: String?,
        thumbUrl: String? = null,
        thumbRes: Int? = null,
        desc: String? = "",
        cb: ShareCallback? = null,
    ) {

        if (!checkInstall(activity, SHARE_MEDIA.WEIXIN)) {
            cb?.onError(Throwable("未安装该应用"))
            "未安装该应用".toast()
            return
        }

        val web = UMWeb(url)
        web.title = title
        if (thumbUrl != null) web.setThumb(UMImage(activity, thumbUrl))
        if (thumbRes != null) web.setThumb(UMImage(activity, thumbRes))
        web.description = desc
        ShareAction(activity)
            .setPlatform(SHARE_MEDIA.WEIXIN)
            .withMedia(web)
            .setCallback(InnerShareCallback(cb = cb))
            .share()
    }


    fun shareImageToWechat(activity: Activity,title: String?,desc: String?,
                           url: String?, cb: ShareCallback? = null,) {
        if (!checkInstall(activity, SHARE_MEDIA.WEIXIN)) {
            cb?.onError(Throwable("未安装该应用"))
            "未安装该应用".toast()
            return
        }
        val umImage = UMImage(activity, url)
        umImage.title = title
        umImage.description = desc
        ShareAction(activity)
            .setPlatform(SHARE_MEDIA.WEIXIN)
            .withMedia(umImage)
            .setCallback(InnerShareCallback(cb = cb))
            .share()
    }


    fun shareVideoToWechat(activity: Activity,title: String?,desc: String?,
                           url: String?, thumbUrl: String?, cb: ShareCallback? = null,) {
        if (!checkInstall(activity, SHARE_MEDIA.WEIXIN)) {
            cb?.onError(Throwable("未安装该应用"))
            "未安装该应用".toast()
            return
        }
        val umImage = UMVideo(url)
        umImage.setThumb(UMImage(activity, thumbUrl))
        umImage.title = title
        umImage.description = desc
        ShareAction(activity)
            .setPlatform(SHARE_MEDIA.WEIXIN)
            .withMedia(umImage)
            .setCallback(InnerShareCallback(cb = cb))
            .share()
    }



    class InnerShareCallback(var cb: ShareCallback?) : UMShareListener {
        override fun onResult(p: SHARE_MEDIA) {
            LogUtils.d("share->onResult $p")
            cb?.onComplete(p)
        }

        override fun onCancel(p: SHARE_MEDIA) {
            LogUtils.d("share->onCancel $p")
            cb?.onCancel(p)
        }

        override fun onError(p: SHARE_MEDIA, t: Throwable) {
            LogUtils.e("share->onError $p  ${t.message}")
            cb?.onError(t)
        }

        override fun onStart(p: SHARE_MEDIA) {
            LogUtils.d("share->onStart $p")
            cb?.onStart(p)
        }
    }

    interface ShareCallback {
        fun onCancel(shareMedia: SHARE_MEDIA) {}
        fun onStart(shareMedia: SHARE_MEDIA) {}
        fun onError(t: Throwable) {}
        fun onComplete(shareMedia: SHARE_MEDIA) {}
    }
}