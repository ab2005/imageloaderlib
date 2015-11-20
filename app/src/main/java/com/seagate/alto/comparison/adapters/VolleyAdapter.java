
package com.seagate.alto.comparison.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.seagate.alto.comparison.configs.volley.SampleVolleyFactory;
import com.seagate.alto.comparison.holders.BaseViewHolder;
import com.seagate.alto.comparison.holders.VolleyHolder;
import com.seagate.alto.comparison.instrumentation.InstrumentedNetworkImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Volley
 */
public class VolleyAdapter extends ImageListAdapter {

    private final ImageLoader mImageLoader;

    public VolleyAdapter(Context context, PerfListener perfListener, AdapterDelegate delegate) {
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
    public void shutDown() {
        super.clear();
        SampleVolleyFactory.getMemoryCache().clear();
    }
}
