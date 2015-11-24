
package com.seagate.imageadapter.holders;

import android.content.Context;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.seagate.imageadapter.instrumentation.InstrumentedNetworkImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * This is the Holder class for the RecycleView to use with Volley
 */
public class VolleyHolder extends BaseViewHolder<InstrumentedNetworkImageView> {

    private final ImageLoader mImageLoader;

    public VolleyHolder(Context context, ImageLoader imageLoader, View layoutView, InstrumentedNetworkImageView view, PerfListener perfListener) {
        super(context, layoutView, view, perfListener);
        mImageLoader = imageLoader;
    }

    @Override
    protected void onBind(String uri) {
        mImageView.setImageUrl(uri, mImageLoader);
    }
}
