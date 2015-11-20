
package com.seagate.alto.comparison.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.seagate.alto.comparison.holders.BaseViewHolder;
import com.seagate.alto.comparison.holders.GlideHolder;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Glide
 */
public class GlideAdapter extends ImageListAdapter {

  public GlideAdapter(Context context, PerfListener perfListener, AdapterDelegate delegate) {
    super(context, perfListener, delegate);
  }

  @Override
  public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
    View holderView = getDelegate().getHolderView(parent, viewType);
    final InstrumentedImageView instrImageView = (InstrumentedImageView) super.getInstrumentedView(holderView);
    return new GlideHolder(getContext(), holderView, instrImageView, getPerfListener());
  }

  @Override
  public void shutDown() {
    Glide.get(getContext()).clearMemory();
  }
}
