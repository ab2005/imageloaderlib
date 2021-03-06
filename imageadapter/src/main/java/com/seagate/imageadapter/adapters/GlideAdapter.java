
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.holders.GlideHolder;
import com.seagate.imageadapter.instrumentation.InstrumentedImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Glide
 */
public class GlideAdapter extends Adapter {

  public GlideAdapter(Context context, PerfListener perfListener, Delegate delegate) {
    super(context, perfListener, delegate);
  }

  @Override
  public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
    View holderView = getDelegate().getHolderView(parent, viewType);
    final InstrumentedImageView instrImageView = (InstrumentedImageView) super.getInstrumentedView(holderView);
    return new GlideHolder(getContext(), holderView, instrImageView, getPerfListener());
  }

  @Override
  public void dispose() {
    Glide.get(getContext()).clearMemory();
  }
}
