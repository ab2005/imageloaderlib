
package com.seagate.alto.comparison.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.seagate.alto.comparison.holders.AQueryHolder;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Android Query
 */
public class AQueryAdapter extends ImageListAdapter {
    private AQuery mAQuery;

    public AQueryAdapter(Context context, PerfListener perfListener, AdapterDelegate delegate) {
        super(context, perfListener, delegate);
        mAQuery = new AQuery(context);
    }

    @Override
    public AQueryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = getDelegate().getHolderView(parent, viewType);
        final InstrumentedImageView instrImageView = (InstrumentedImageView) super.getInstrumentedView(holderView);
        return new AQueryHolder(getContext(), mAQuery, holderView, instrImageView, getPerfListener());
    }

    @Override
    public void shutDown() {
        for (int i = 0; i < getItemCount(); i++) {
            String uri = getItem(i);
            mAQuery.invalidate(uri);
        }
        super.clear();
    }
}
