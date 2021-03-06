
package com.seagate.imageadapter.holders;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.seagate.imageadapter.Drawees;
import com.seagate.imageadapter.instrumentation.InstrumentedImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;


/**
 * This is the Holder class for the RecycleView to use with Glide
 */
public class GlideHolder extends BaseViewHolder<InstrumentedImageView> {

    public GlideHolder(
            Context context, View layoutView,
            InstrumentedImageView instrumentedImageView, PerfListener perfListener) {
        super(context, layoutView, instrumentedImageView, perfListener);
    }

    @Override
    protected void onBind(String uri) {
        Glide.with(mImageView.getContext())
                .load(uri)
                .placeholder(Drawees.sPlaceholderDrawable)
                .error(Drawees.sErrorDrawable)
                .crossFade()
                .into(mImageView);
    }

}
