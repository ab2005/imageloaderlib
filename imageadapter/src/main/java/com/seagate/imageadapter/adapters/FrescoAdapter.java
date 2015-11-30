
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.holders.FrescoHolder;
import com.seagate.imageadapter.instrumentation.InstrumentedDraweeView;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Fresco
 */
public class FrescoAdapter extends Adapter {

    public FrescoAdapter(Context context, PerfListener perfListener, ImagePipelineConfig imagePipelineConfig, Delegate ad) {
        super(context, perfListener, ad);
        Fresco.initialize(context, imagePipelineConfig);
    }

    @Override
    public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = getDelegate().getHolderView(parent, viewType);
        final InstrumentedDraweeView instrView = (InstrumentedDraweeView) super.getInstrumentedView(holderView);
        return new FrescoHolder(getContext(), holderView, instrView, getPerfListener());
    }

    @Override
    public void dispose() {
        Fresco.shutDown();
    }
}
