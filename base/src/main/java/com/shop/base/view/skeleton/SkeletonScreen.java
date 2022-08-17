package com.shop.base.view.skeleton;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author dxl
 */

public interface SkeletonScreen {

    void show();

    void hide();

    /**
     * dxl
     * 在fragment切换时，会出现recyclerView UI显示为空的问题
     * 经过排查，发现在SkeletonScreen在调用hide时，if (mRecyclerView.getAdapter() != mActualAdapter) mRecyclerView.setAdapter(mActualAdapter);
     * 判断条件是false，但是这个mRecyclerView已经不是fragment真实的recyclerView了，真实的recyclerView已经被回收，fragment回来又重建了
     * 调用mRecyclerView.isAttachedToWindow已经是false了
     * 所以这里在hide的时候，把当前的recyclerView重新传过去进行替换，可以解决问题
     * 临时先这样，后续研究一下更好的实现方式
     */
    void hide(RecyclerView recyclerView);
}
