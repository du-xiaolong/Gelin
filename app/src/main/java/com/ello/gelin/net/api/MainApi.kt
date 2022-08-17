package com.ello.gelin.net.api

import com.ello.gelin.net.ApiResult
import com.ello.gelin.ui.moment.list.Moment
import com.ello.gelin.ui.moment.list.PageWrapper
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
interface MainApi {

    @GET("moment/index")
    suspend fun getMomentList(@Query("pageNum") page: Int): ApiResult<PageWrapper<Moment>>
}