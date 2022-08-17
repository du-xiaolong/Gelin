package com.ello.gelin.hiltModule

import com.ello.gelin.BuildConfig
import com.ello.gelin.net.LogInterceptor
import com.ello.gelin.net.UrlConstant
import com.ello.gelin.net.api.MainApi
import com.ello.gelin.user.UserManager
import com.shop.base.common.DApp
import com.shop.base.util.isNetworkAvailable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @DYT
    @Provides
    fun provideDYTOkHttpClient(): OkHttpClient {
        return getOkHttpClient(UserManager.dytCookie)
    }

    @DLC
    @Provides
    fun provideDLCOkHttpClient(): OkHttpClient {
        return getOkHttpClient(UserManager.dlcCookie)
    }

    @DYT
    @Provides
    fun provideDYTApi(@DYT okHttpClient: OkHttpClient): MainApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(UrlConstant.ROOT_URL)
            .build().create(MainApi::class.java)
    }

    @DLC
    @Provides
    fun provideDLCApi(@DLC okHttpClient: OkHttpClient): MainApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(UrlConstant.ROOT_URL)
            .build().create(MainApi::class.java)
    }

    private fun getOkHttpClient(cookie: String): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .callTimeout(60, TimeUnit.SECONDS)
            .cache(Cache(File(DApp.instance.cacheDir, "sh_request"), 1024 * 1024 * 100L))
            .addInterceptor(Interceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder: Request.Builder = originalRequest.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Cookie", cookie)

                requestBuilder.method(originalRequest.method, originalRequest.body)
                val request = requestBuilder.build()
                chain.proceed(request)
            })

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(LogInterceptor())
        }
        builder.addInterceptor { chain ->
            val request = chain.request()
            if (isNetworkAvailable(DApp.instance)) {
                /*
                 *  If there is Internet, get the cache that was stored 5 seconds ago.
                 *  If the cache is older than 5 seconds, then discard it,
                 *  and indicate an error in fetching the response.
                 *  The 'max-age' attribute is responsible for this behavior.
                 */
                request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
            } else {
                /*
                 *  If there is no Internet, get the cache that was stored 7 days ago.
                 *  If the cache is older than 7 days, then discard it,
                 *  and indicate an error in fetching the response.
                 *  The 'max-stale' attribute is responsible for this behavior.
                 *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                 */
                request.newBuilder()
                    .header(
                        "Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
                    )
                    .build()
            }

            chain.proceed(request)
        }
        return builder.build()

    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DYT

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DLC