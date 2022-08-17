package com.shop.base.view.skeleton;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.shop.base.R;


/**
 * @author dxl
 */

public class ViewSkeletonScreen implements SkeletonScreen {
    private static final String TAG = ViewSkeletonScreen.class.getName();
    private final ViewReplacer mViewReplacer;
    private final View mActualView;
    private final int mSkeletonResID;
    private final boolean mShimmer;

    private ViewSkeletonScreen(Builder builder) {
        mActualView = builder.mView;
        mSkeletonResID = builder.mSkeletonLayoutResID;
        mShimmer = builder.mShimmer;
        mViewReplacer = new ViewReplacer(builder.mView);
    }

    private ShimmerFrameLayout generateShimmerContainerLayout(ViewGroup parentView) {
        final ShimmerFrameLayout shimmerLayout = (ShimmerFrameLayout) LayoutInflater.from(mActualView.getContext()).inflate(R.layout.layout_shimmer, parentView, false);
        View innerView = LayoutInflater.from(mActualView.getContext()).inflate(mSkeletonResID, shimmerLayout, false);
        ViewGroup.LayoutParams lp = innerView.getLayoutParams();
        if (lp != null) {
            shimmerLayout.setLayoutParams(lp);
        }
        shimmerLayout.addView(innerView);
        shimmerLayout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                shimmerLayout.startShimmer();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                shimmerLayout.stopShimmer();
            }
        });
        shimmerLayout.startShimmer();
        return shimmerLayout;
    }

    private View generateSkeletonLoadingView() {
        ViewParent viewParent = mActualView.getParent();
        if (viewParent == null) {
            Log.e(TAG, "the source view have not attach to any view");
            return null;
        }
        ViewGroup parentView = (ViewGroup) viewParent;
        if (mShimmer) {
            return generateShimmerContainerLayout(parentView);
        }
        return LayoutInflater.from(mActualView.getContext()).inflate(mSkeletonResID, parentView, false);
    }

    @Override
    public void show() {
        View skeletonLoadingView = generateSkeletonLoadingView();
        if (skeletonLoadingView != null) {
            mViewReplacer.replace(skeletonLoadingView);
        }
    }

    @Override
    public void hide() {
        if (mViewReplacer.getTargetView() instanceof ShimmerFrameLayout) {
            ((ShimmerFrameLayout) mViewReplacer.getTargetView()).stopShimmer();
        }
        mViewReplacer.restore();
    }

    @Override
    public void hide(RecyclerView recyclerView) {

    }

    public static class Builder {
        private final View mView;
        private int mSkeletonLayoutResID;
        private boolean mShimmer = true;

        public Builder(View view) {
            this.mView = view;
        }

        /**
         * @param skeletonLayoutResID the loading skeleton layoutResID
         */
        public Builder load(@LayoutRes int skeletonLayoutResID) {
            this.mSkeletonLayoutResID = skeletonLayoutResID;
            return this;
        }

        /**
         * @param shimmer whether show shimmer animation
         */
        public Builder shimmer(boolean shimmer) {
            this.mShimmer = shimmer;
            return this;
        }

        public ViewSkeletonScreen build() {
            return new ViewSkeletonScreen(this);
        }

    }
}
