package com.shop.base.common

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.DownloadListener
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import com.download.library.DownloadImpl
import com.download.library.DownloadListenerAdapter
import com.download.library.Extra
import com.download.library.ResourceRequest
import com.just.agentweb.*
import com.shop.base.R
import com.shop.base.databinding.ActivityBaseWebViewBinding
import com.shop.base.ext.lllog

/**
 * webView
 * @author dxl
 * @date 2022-03-21
 */
class BaseWebViewActivity :
    BaseVbActivity<BaseViewModel, ActivityBaseWebViewBinding>() {


    private var url: String = ""
    private var agentWeb: AgentWeb? = null

    companion object {

        fun start(context: Context?, url: String, headers: HashMap<String, String>) {
            context?.startActivity(Intent(context, BaseWebViewActivity::class.java).apply {
                putExtra("url", url)
                putExtra("headers", headers)
            })
        }

        fun start(context: Context?, url: String) {
            context?.startActivity(Intent(context, BaseWebViewActivity::class.java).apply {
                putExtra("url", url)
            })
        }

    }


    @SuppressLint("SetJavaScriptEnabled")
    override fun init(savedInstanceState: Bundle?) {

        initView()
        url = intent.getStringExtra("url") ?: "about:blank"

        agentWeb = AgentWeb.with(this).setAgentWebParent(
            vb.llContainer,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        ).useDefaultIndicator(Color.parseColor("#cc000000"), 1)
            .setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    url = request?.url.toString()
                    if (!url.startsWith("http")) {
                        //跳转外部应用
                        kotlin.runCatching {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            startActivity(intent)
                        }
                        return true
                    }
                    return false

                }
            })
            .setWebChromeClient(object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    vb.toolbar.title = title
                }
            })
            ////拦截找不到相关页面的Scheme
            .interceptUnkownUrl()
            .setAgentWebWebSettings(getSettings())
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
            .setMainFrameErrorView(R.layout.layout_webview_error, R.id.btnRetry)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .createAgentWeb()
            .ready()
            .get()
        agentWeb?.webCreator?.webView?.settings?.apply {
            javaScriptEnabled = true
            setSupportZoom(true)
            builtInZoomControls = false
            cacheMode = if (AgentWebUtils.checkNetwork(this@BaseWebViewActivity)) {
                //根据cache-control获取数据。
                WebSettings.LOAD_DEFAULT
            } else {
                //没网，则从本地获取，即离线加载
                WebSettings.LOAD_CACHE_ELSE_NETWORK
            }
            //适配5.0不允许http和https混合使用情况
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            textZoom = 100
            databaseEnabled = true
            setAppCacheEnabled(true)
            loadsImagesAutomatically = true
            setSupportMultipleWindows(false)
            blockNetworkImage = false;//是否阻塞加载网络图片  协议http or https
            allowFileAccess = true //允许加载本地文件html  file协议, 这可能会造成不安全 , 建议重写关闭

            allowFileAccessFromFileURLs = false //通过 file url 加载的 Javascript 读取其他的本地文件 .建议关闭

            allowUniversalAccessFromFileURLs =
                false //允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源

            javaScriptCanOpenWindowsAutomatically = true

            loadWithOverviewMode = true
            useWideViewPort = true
            domStorageEnabled = true
            setNeedInitialFocus(true)
            defaultTextEncodingName = "utf-8" //设置编码格式

            defaultFontSize = 16
            minimumFontSize = 8 //设置 WebView 支持的最小字体大小，默认为 8

            setGeolocationEnabled(true)

            setAppCachePath(AgentWebConfig.getCachePath(this@BaseWebViewActivity))
        }
        agentWeb?.urlLoader?.loadUrl(url)
    }

    private fun getSettings(): IAgentWebSettings<WebSettings> {
        return object : AbsAgentWebSettings() {

            override fun setDownloader(
                webView: WebView?,
                downloadListener: DownloadListener?
            ): WebListenerManager {
                return super.setDownloader(
                    webView,
                    object : DefaultDownloadImpl(
                        this@BaseWebViewActivity,
                        webView,
                        agentWeb?.permissionInterceptor
                    ) {
                        override fun createResourceRequest(url: String): ResourceRequest<*> {
                            return DownloadImpl.getInstance(this@BaseWebViewActivity)
                                .url(url)
                                .quickProgress()       //进度条快速刷新
                                .autoOpenIgnoreMD5()  //下载完成直接打开
                                .setEnableIndicator(true)  //显示系统通知
                                .setRetry(5)                //下载失败重试次数
                        }

                        override fun checkNeedPermission(): MutableList<String> {
                            return super.checkNeedPermission()
                        }


                        override fun taskEnqueue(resourceRequest: ResourceRequest<*>) {
                            resourceRequest.enqueue(object : DownloadListenerAdapter() {

                                override fun onStart(
                                    url: String?,
                                    userAgent: String?,
                                    contentDisposition: String?,
                                    mimetype: String?,
                                    contentLength: Long,
                                    extra: Extra?
                                ) {
                                    super.onStart(
                                        url,
                                        userAgent,
                                        contentDisposition,
                                        mimetype,
                                        contentLength,
                                        extra
                                    )
                                    lllog("onStart - $mimetype - contentLength=$contentLength")
                                }

                                override fun onProgress(
                                    url: String?,
                                    downloaded: Long,
                                    length: Long,
                                    usedTime: Long
                                ) {

                                    super.onProgress(url, downloaded, length, usedTime)
                                    lllog("onProgress - download = $downloaded - length=$length -userTime= $usedTime")
                                }

                                override fun onResult(
                                    throwable: Throwable?,
                                    path: Uri?,
                                    url: String?,
                                    extra: Extra?
                                ): Boolean {
                                    lllog("onResult - path = $path")
                                    return super.onResult(throwable, path, url, extra)
                                }
                            })


                        }


                    })
            }

            override fun bindAgentWebSupport(agentWeb: AgentWeb?) {

            }

        }
    }

    private fun initView() {
        vb.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        if (agentWeb?.back() == false) {
            setResult(RESULT_OK)
            super.onBackPressed()
        }
    }

    override fun onPause() {
        agentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onResume() {
        agentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb?.webLifeCycle?.onDestroy()
        super.onDestroy()
    }

}