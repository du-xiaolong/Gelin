package com.ello.gelin.ui.moment.list

import androidx.core.os.bundleOf
import androidx.paging.LoadState
import com.ello.gelin.R
import com.ello.gelin.common.FooterAdapter
import com.ello.gelin.databinding.FragmentMomentListBinding
import com.shop.base.common.BaseDbFragment
import com.shop.base.ext.Params
import com.shop.base.ext.addDivider
import com.shop.base.ext.repeatOnStart
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * @author dxl
 * @date 2022-08-16  周二
 */
@AndroidEntryPoint
class MomentListFragment :
    BaseDbFragment<MomentViewModel, FragmentMomentListBinding>(R.layout.fragment_moment_list) {

    private val name: String? by Params("name")

    @Inject
    lateinit var adapter: MomentListAdapter

    companion object {
        fun newInstance(name: String) = MomentListFragment().apply {
            arguments = bundleOf("name" to name)
        }
    }

    override fun initView() {
        vb.rvList.addDivider(paddingLeftDp = 15f, paddingRightDp = 15f)
        vb.rvList.adapter = adapter.withLoadStateFooter(FooterAdapter {
            adapter.retry()
        })

        vb.srlList.setOnRefreshListener {
            adapter.refresh()
        }

        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Loading -> {
                    if (!vb.srlList.isRefreshing) {
                        vb.srlList.isRefreshing = true
                    }
                }
                else -> {
                    vb.srlList.isRefreshing = false
                }
            }
        }
    }

    override fun initData() {
        repeatOnStart {
            val flow =
                if (name == "杜雨桐") viewModel.dytMomentListFlow else viewModel.dlcMomentListFlow
            flow.collectLatest {
                adapter.submitData(it)
            }
        }
    }


}



