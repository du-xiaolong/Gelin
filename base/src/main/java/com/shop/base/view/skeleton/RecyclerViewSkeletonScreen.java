package com.shop.base.view.skeleton;


import androidx.annotation.ArrayRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.shop.base.R;


/**
 * 参考 https://github.com/ethanhua/Skeleton
 *
 * @author dxl
 */
public class RecyclerViewSkeletonScreen implements SkeletonScreen {

    private RecyclerView mRecyclerView;
    private final RecyclerView.Adapter<?> mActualAdapter;
    private final SkeletonAdapter mSkeletonAdapter;
    private final boolean mRecyclerViewFrozen;

    private RecyclerViewSkeletonScreen(Builder builder) {
        mRecyclerView = builder.mRecyclerView;
        mActualAdapter = builder.mActualAdapter;
        mSkeletonAdapter = new SkeletonAdapter();
        mSkeletonAdapter.setItemCount(builder.mItemCount);
        mSkeletonAdapter.setLayoutReference(builder.mItemResID);
        mSkeletonAdapter.setArrayOfLayoutReferences(builder.mItemsResIDArray);
        mSkeletonAdapter.shimmer(builder.mShimmer);
        mRecyclerViewFrozen = builder.mFrozen;
    }

    @Override
    public void show() {
        mRecyclerView.setAdapter(mSkeletonAdapter);
        if (!mRecyclerView.isComputingLayout() && mRecyclerViewFrozen) {
            mRecyclerView.suppressLayout(true);
        }
    }

    @Override
    public void hide() {
        if (mRecyclerView.getAdapter() != mActualAdapter) {
            mRecyclerView.setAdapter(mActualAdapter);
        }
    }

    @Override
    public void hide(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        if (mRecyclerView.getAdapter() != mActualAdapter) {
            mRecyclerView.setAdapter(mActualAdapter);
        }
    }

    public static class Builder {
        private RecyclerView.Adapter<?> mActualAdapter;
        private final RecyclerView mRecyclerView;
        private boolean mShimmer = true;
        private int mItemCount = 10;
        private int mItemResID = R.layout.layout_default_item_skeleton;
        private int[] mItemsResIDArray;
        private boolean mFrozen = true;

        public Builder(RecyclerView recyclerView) {
            this.mRecyclerView = recyclerView;
        }

        /**
         * @param adapter the target recyclerView actual adapter
         */
        public Builder adapter(RecyclerView.Adapter<?> adapter) {
            this.mActualAdapter = adapter;
            return this;
        }

        /**
         * @param itemCount the child item count in recyclerView
         */
        public Builder count(int itemCount) {
            this.mItemCount = itemCount;
            return this;
        }

        /**
         * @param shimmer whether show shimmer animation
         */
        public Builder shimmer(boolean shimmer) {
            this.mShimmer = shimmer;
            return this;
        }

        /**
         * @param skeletonLayoutResID the loading skeleton layoutResID
         */
        public Builder load(@LayoutRes int skeletonLayoutResID) {
            this.mItemResID = skeletonLayoutResID;
            return this;
        }

        /**
         * @param skeletonLayoutResIDs the loading array of skeleton layoutResID
         */
        public Builder loadArrayOfLayouts(@ArrayRes int[] skeletonLayoutResIDs) {
            this.mItemsResIDArray = skeletonLayoutResIDs;
            return this;
        }

        /**
         * @param frozen whether frozen recyclerView during skeleton showing
         * @return
         */
        public Builder frozen(boolean frozen) {
            this.mFrozen = frozen;
            return this;
        }

        public RecyclerViewSkeletonScreen build() {
            return new RecyclerViewSkeletonScreen(this);
        }

        @Deprecated
        public RecyclerViewSkeletonScreen show() {
            RecyclerViewSkeletonScreen recyclerViewSkeleton = new RecyclerViewSkeletonScreen(this);
            recyclerViewSkeleton.show();
            return recyclerViewSkeleton;
        }
    }
}
