package com.ello.gelin.common

import androidx.paging.PagingSource
import androidx.paging.PagingState

/**
 * @author dxl
 * @date 2022-08-17  周三
 */
class IntKeyPagingSource<T : Any>(
    private val pageStart: Int = 1,
    private val loadData:suspend (Int, Int) -> List<T>
) : PagingSource<Int, T>() {

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        val page = params.key ?: pageStart
        return kotlin.runCatching {
            val data = loadData(page, params.loadSize)
            LoadResult.Page(
                data = data,
                prevKey = if (page == pageStart) null else page - 1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        }.getOrElse {
            LoadResult.Error(it)
        }
    }
}