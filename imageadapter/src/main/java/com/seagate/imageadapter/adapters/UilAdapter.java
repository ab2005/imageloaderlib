
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seagate.imageadapter.configs.uil.SampleUilFactory;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.holders.UilHolder;
import com.seagate.imageadapter.instrumentation.InstrumentedImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Universal ImageLoader
 */
public class UilAdapter extends Adapter {

  private final ImageLoader mImageLoader;

  public UilAdapter(Context context, PerfListener perfListener, Delegate delegate) {
    super(context, perfListener, delegate);
    mImageLoader = SampleUilFactory.getImageLoader(context);
  }

  @Override
  public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
    View holderView = getDelegate().getHolderView(parent, viewType);
    final InstrumentedImageView instrImageView = (InstrumentedImageView) super.getInstrumentedView(holderView);
    return new UilHolder(getContext(), mImageLoader, holderView, instrImageView, getPerfListener());
  }

  @Override
  public void dispose() {
    super.clear();
    mImageLoader.clearMemoryCache();
  }
}
