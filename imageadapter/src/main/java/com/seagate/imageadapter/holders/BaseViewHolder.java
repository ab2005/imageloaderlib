/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.imageadapter.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.seagate.imageadapter.instrumentation.Instrumented;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * The base ViewHolder with instrumentation
 */
public abstract class BaseViewHolder<V extends View & Instrumented> extends RecyclerView.ViewHolder {
    private final PerfListener mPerfListener;
    private final View mParentView;
    protected final V mImageView;
    private Context mContext;

    /**
     * @param parentView - list item container
     * @param imageView - instrumented view to handle image                  
     */
    public BaseViewHolder(Context context, View parentView, V imageView, PerfListener perfListener) {
        super(parentView);
        this.mContext = context;
        this.mPerfListener = perfListener;
        this.mParentView = parentView;
        this.mImageView = imageView;
//        int size = calcDesiredSize(mParentView.getWidth(), mParentView.getHeight());
//        updateViewLayoutParams(mImageView, size, size);
    }

    // Called from Adapter #link: onBindViewHolder() to provide data for the view
    public void bind(String model) {
        mImageView.initInstrumentation(model.toString(), mPerfListener);
        //itemView.setTag(Adapter.Delegate.KEY_DATA, model);
        onBind(model);
    }

    /**
     * Load an image of the specified uri into the view, asynchronously.
     */
    protected abstract void onBind(String uri);

    protected Context getContext() {
        return mContext;
    }

    private void updateViewLayoutParams(View view, int width, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        if (layoutParams == null || layoutParams.height != width || layoutParams.width != height) {
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(width, height);
            view.setLayoutParams(layoutParams);
        }
    }

//    private int calcDesiredSize(int parentWidth, int parentHeight) {
//        return MainActivity.calcDesiredSize(mContext, parentWidth, parentHeight);
//    }
}
