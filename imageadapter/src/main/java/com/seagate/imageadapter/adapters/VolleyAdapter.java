
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.seagate.imageadapter.configs.volley.SampleVolleyFactory;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.holders.VolleyHolder;
import com.seagate.imageadapter.instrumentation.InstrumentedNetworkImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Volley
 */
public class VolleyAdapter extends Adapter {

    private final ImageLoader mImageLoader;

    public VolleyAdapter(Context context, PerfListener perfListener, Delegate delegate) {
        super(context, perfListener, delegate);
        mImageLoader = SampleVolleyFactory.getImageLoader(context);
    }

    @Override
    public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = getDelegate().getHolderView(parent, viewType);
        final InstrumentedNetworkImageView instrImageView = (InstrumentedNetworkImageView) super.getInstrumentedView(holderView);
        return new VolleyHolder(getContext(), mImageLoader, holderView, instrImageView, getPerfListener());
    }

    @Override
    public void dispose() {
        super.clear();
        SampleVolleyFactory.getMemoryCache().clear();
    }
}
