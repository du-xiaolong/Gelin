package com.ello.gelin.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ello.gelin.databinding.ViewLoadMoreBinding
import com.shop.base.ext.click
import com.shop.base.ext.lllog

/**
 * @author dxl
 * @date 2022-07-29  周五
 */
class FooterAdapter(val retry: () -> Unit) : LoadStateAdapter<FooterAdapter.FooterViewHolder>() {


    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading ||
                loadState is LoadState.Error ||
                (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
    }

    override fun onBindViewHolder(holder: FooterViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): FooterViewHolder =
        FooterViewHolder(
            ViewLoadMoreBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    inner class FooterViewHolder(private val viewBinding: ViewLoadMoreBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(loadState: LoadState) {
            lllog("footer状态 = $loadState")
            when (loadState) {
                is LoadState.Loading -> {
                    viewBinding.root.isVisible = true
                    viewBinding.loadMoreLoadEndView.isVisible = false
                    viewBinding.loadMoreLoadCompleteView.isVisible = false
                    viewBinding.loadMoreLoadingView.isVisible = true
                    viewBinding.loadMoreLoadFailView.isVisible = false
                }
                is LoadState.NotLoading -> {
                    if (loadState.endOfPaginationReached) {
                        viewBinding.root.isVisible = true
                        viewBinding.loadMoreLoadEndView.isVisible = true
                        viewBinding.loadMoreLoadCompleteView.isVisible = false
                        viewBinding.loadMoreLoadingView.isVisible = false
                        viewBinding.loadMoreLoadFailView.isVisible = false
                    } else {
                        viewBinding.root.isVisible = false
                    }
                }
                is LoadState.Error -> {
                    viewBinding.root.isVisible = true
                    viewBinding.loadMoreLoadEndView.isVisible = false
                    viewBinding.loadMoreLoadCompleteView.isVisible = false
                    viewBinding.loadMoreLoadingView.isVisible = false
                    viewBinding.loadMoreLoadFailView.isVisible = true
                    viewBinding.root.click { retry.invoke() }
                }
                else -> {
                    viewBinding.root.isVisible = false
                }
            }
        }

    }
}