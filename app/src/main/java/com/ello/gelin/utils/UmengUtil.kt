package com.ello.gelin.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.LogUtils
import com.ello.gelin.BuildConfig
import com.shop.base.ext.lllog
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
import com.umeng.socialize.shareboard.ShareBoardConfig
import java.io.File


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
        PlatformConfig.setWXFileProvider("${BuildConfig.APPLICATION_ID}.provider")

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



    fun shareImageToWechat(
        activity: Activity, title: String?, desc: String?,
        url: String?, cb: ShareCallback? = null,
    ) {





        DownloadUtils.download(url, File(activity.externalCacheDir, "1.jpg"), object :DownloadUtils.DownloadListener{
            override fun onResult(result: Result<File>) {
                lllog("下载图片：$result")
                if (result.isSuccess) {
                    val intent = Intent()
                    val componentName = ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
                    intent.component = componentName
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    intent.action = Intent.ACTION_SEND
                    intent.type = "image/*";
                    val file = result.getOrThrow()
                    val uri = FileProvider.getUriForFile(
                        activity,
                        activity.packageName + ".provider",
                        file
                    )

                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    activity.startActivity(Intent.createChooser(intent, "发送到"))
                }
            }
            override fun onProgress(progress: Int) {
                lllog("下载图片进度：$progress")
            }
        })

//        val umImage = UMImage(activity, "https://file.1d1d100.com/2022/8/26/6af340916bd54aca857c55ff99c03852.jpeg")
//
//        ShareAction(activity)
//            .setPlatform(SHARE_MEDIA.WEIXIN)
//            .withMedia(UMImage(activity, "https://file.1d1d100.com/2022/8/26/6af340916bd54aca857c55ff99c03852.jpeg"))
//            .share()
    }


    fun shareVideoToWechat(
        activity: Activity, title: String?, desc: String?,
        url: String?, thumbUrl: String?, cb: ShareCallback? = null,
    ) {
        if (!checkInstall(activity, SHARE_MEDIA.WEIXIN)) {
            cb?.onError(Throwable("未安装该应用"))
            "未安装该应用".toast()
            return
        }
        DownloadUtils.download(url, File(activity.externalCacheDir, "1.mp4"), object :DownloadUtils.DownloadListener{
            override fun onResult(result: Result<File>) {
                lllog("下载图片：$result")
                if (result.isSuccess) {
                    val intent = Intent()
                    val componentName = ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI")
                    intent.component = componentName
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    intent.action = Intent.ACTION_SEND
                    intent.type = "video/*"
                    val file = result.getOrThrow()
                    val uri = FileProvider.getUriForFile(
                        activity,
                        activity.packageName + ".provider",
                        file
                    )

                    intent.putExtra(Intent.EXTRA_STREAM, uri)
                    activity.startActivity(Intent.createChooser(intent, "发送到"))
                }
            }
            override fun onProgress(progress: Int) {
                lllog("下载图片进度：$progress")
            }
        })



//        val umImage = UMVideo(url)
//        umImage.setThumb(UMImage(activity, thumbUrl))
//        umImage.title = title
//        umImage.description = desc
//        ShareAction(activity)
//            .setPlatform(SHARE_MEDIA.WEIXIN)
//            .withMedia(umImage)
//            .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
//            .setCallback(InnerShareCallback(cb = cb))
//            .open()
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