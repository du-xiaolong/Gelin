package com.ello.gelin.ui.moment.list

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ello.gelin.common.IntKeyPagingSource
import com.ello.gelin.hiltModule.DLC
import com.ello.gelin.hiltModule.DYT
import com.ello.gelin.net.api.MainApi
import com.shop.base.ext.requireNotNull
import com.shop.base.net.requireSuccess
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
class MomentRepo @Inject constructor() {

    @DYT
    @Inject
    lateinit var dytApi: MainApi

    @DLC
    @Inject
    lateinit var dlcApi: MainApi


    fun requestList(name: String): Flow<PagingData<Moment>> {
        val api = if (name == "dyt") dytApi else dlcApi
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 10,
                enablePlaceholders = false
            )
        ) {
            IntKeyPagingSource { page, _ ->
                api.getMomentList(page).requireSuccess().data?.list.requireNotNull()
            }
        }.flow
    }


}