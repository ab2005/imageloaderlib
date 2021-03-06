
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.imageadapter.backend.volley.VolleyDraweeControllerBuilderSupplier;
import com.seagate.imageadapter.configs.volley.SampleVolleyFactory;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.holders.VolleyDraweeHolder;
import com.seagate.imageadapter.instrumentation.InstrumentedDraweeView;
import com.seagate.imageadapter.instrumentation.PerfListener;

/**
 * RecyclerView Adapter for Volley using Drawee
 */
public class VolleyDraweeAdapter extends Adapter {

    public VolleyDraweeAdapter(Context context, PerfListener perfListener, Delegate delegate) {
        super(context, perfListener, delegate);
        final VolleyDraweeControllerBuilderSupplier supplier =
                new VolleyDraweeControllerBuilderSupplier(
                        context,
                        SampleVolleyFactory.getImageLoader(context));
        InstrumentedDraweeView.initialize(supplier);
    }

    @Override
    public BaseViewHolder<?> onCreateViewHolder(ViewGroup parent, int viewType) {
//        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(getContext().getResources())
//                .setPlaceholderImage(Drawables.sPlaceholderDrawable)
//                .setFailureImage(Drawables.sErrorDrawable)
//                .build();
//        InstrumentedDraweeView view = new InstrumentedDraweeView(getContext());
//        view.setHierarchy(gdh);
//        return new VolleyDraweeHolder(getContext(), parent, view, getPerfListener());

        View holderView = getDelegate().getHolderView(parent, viewType);
        InstrumentedDraweeView instrView = (InstrumentedDraweeView) super.getInstrumentedView(holderView);
        return new VolleyDraweeHolder(getContext(), holderView, instrView, getPerfListener());
    }

    @Override
    public void dispose() {
        super.clear();
        InstrumentedDraweeView.shutDown();
        SampleVolleyFactory.getMemoryCache().clear();
    }
}
