
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.imageadapter.configs.picasso.SamplePicassoFactory;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.holders.PicassoHolder;
import com.seagate.imageadapter.instrumentation.InstrumentedImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;
import com.squareup.picasso.Picasso;

/**
 * RecyclerView Adapter for Picasso
 */
public class PicassoAdapter extends Adapter {

  private final Picasso mPicasso;

  public PicassoAdapter(Context context, PerfListener perfListener, Delegate delegate) {
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
  public void dispose() {
    for (int i = 0; i < getItemCount(); i++) {
      String uri = getItem(i);
      mPicasso.invalidate(uri);
    }
    super.clear();
  }
}
