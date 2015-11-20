
package com.seagate.alto.comparison.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.seagate.alto.comparison.configs.uil.SampleUilFactory;
import com.seagate.alto.comparison.holders.BaseViewHolder;
import com.seagate.alto.comparison.holders.UilHolder;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Universal ImageLoader
 */
public class UilAdapter extends ImageListAdapter {

  private final ImageLoader mImageLoader;

  public UilAdapter(Context context, PerfListener perfListener, AdapterDelegate delegate) {
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
  public void shutDown() {
    super.clear();
    mImageLoader.clearMemoryCache();
  }
}
