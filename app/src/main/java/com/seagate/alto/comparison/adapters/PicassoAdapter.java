
package com.seagate.alto.comparison.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.alto.comparison.configs.picasso.SamplePicassoFactory;
import com.seagate.alto.comparison.holders.BaseViewHolder;
import com.seagate.alto.comparison.holders.PicassoHolder;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;
import com.squareup.picasso.Picasso;

/**
 * RecyclerView Adapter for Picasso
 */
public class PicassoAdapter extends ImageListAdapter {

  private final Picasso mPicasso;

  public PicassoAdapter(Context context, PerfListener perfListener, AdapterDelegate delegate) {
    super(context, perfListener, delegate);
    mPicasso = SamplePicassoFactory.getPicasso(context);
  }

  @Override
  public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
    View holderView = getDelegate().getHolderView(parent, viewType);
    final InstrumentedImageView instrImageView = (InstrumentedImageView) super.getInstrumentedView(holderView);
    return new PicassoHolder(getContext(), mPicasso, holderView, instrImageView, getPerfListener());
  }

  @Override
  public void shutDown() {
    for (int i = 0; i < getItemCount(); i++) {
      String uri = getItem(i);
      mPicasso.invalidate(uri);
    }
    super.clear();
  }
}
