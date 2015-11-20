
package com.seagate.alto.comparison.holders;

import android.content.Context;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * This is the Holder class for the RecycleView to use with Universal Image Loader
 */
public class UilHolder extends BaseViewHolder<InstrumentedImageView> {

    private final ImageLoader mImageLoader;

    public UilHolder(
            Context context, ImageLoader imageLoader, View layoutView,
            InstrumentedImageView view, PerfListener perfListener) {
        super(context, layoutView, view, perfListener);
        this.mImageLoader = imageLoader;
    }

    @Override
    protected void onBind(String uri) {
        mImageLoader.displayImage(uri, mImageView);
    }
}
