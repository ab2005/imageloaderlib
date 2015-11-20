
package com.seagate.alto.comparison.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.seagate.alto.R;
import com.seagate.alto.comparison.holders.BaseViewHolder;
import com.seagate.alto.comparison.instrumentation.PerfListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for RecyclerView Adapters
 */
public abstract class ImageListAdapter extends RecyclerView.Adapter<BaseViewHolder<?>> {

    private final PerfListener mPerfListener;

    private final Context mContext;
    private final AdapterDelegate mDelegate;
    private final List<String> mModel;

    public ImageListAdapter(final Context context, final PerfListener perfListener, AdapterDelegate delegate) {
        this.mContext = context;
        this.mPerfListener = perfListener;
        this.mModel = new LinkedList<String>();
        this.mDelegate = delegate;
    }

    public void addUrl(final String url) {
        mModel.add(url);
    }

    protected PerfListener getPerfListener() {
        return mPerfListener;
    }

    protected String getItem(final int position) {
        return mModel.get(position);
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    protected Context getContext() {
        return mContext;
    }

    protected AdapterDelegate getDelegate() {
        return mDelegate;
    }

    public void clear() {
        mModel.clear();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<?> holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * Releases any resources and tears down the adapter.
     */
    public abstract void shutDown();

    protected View getInstrumentedView(View parent) {
        return parent.findViewById(R.id.instr_image);
    }
}
