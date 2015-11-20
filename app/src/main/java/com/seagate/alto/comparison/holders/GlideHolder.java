
package com.seagate.alto.comparison.holders;

import android.content.Context;
import android.view.View;

import com.bumptech.glide.Glide;
import com.seagate.alto.comparison.Drawables;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;


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
                .placeholder(Drawables.sPlaceholderDrawable)
                .error(Drawables.sErrorDrawable)
                .crossFade()
                .into(mImageView);
    }

}
