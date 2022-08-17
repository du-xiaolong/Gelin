package com.ello.gelin.ui.moment.list

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.shop.base.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
@HiltViewModel
class MomentViewModel @Inject constructor(momentRepo: MomentRepo) : BaseViewModel() {

    val dytMomentListFlow = momentRepo.requestList("dyt").cachedIn(viewModelScope)

    val dlcMomentListFlow = momentRepo.requestList("dlc").cachedIn(viewModelScope)

}