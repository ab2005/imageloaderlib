
package com.seagate.alto.comparison.holders;

import android.content.Context;
import android.view.View;

import com.seagate.alto.comparison.Drawables;
import com.seagate.alto.comparison.instrumentation.InstrumentedImageView;
import com.seagate.alto.comparison.instrumentation.PerfListener;
import com.squareup.picasso.Picasso;

/**
 * This is the Holder class for the RecycleView to use with Picasso
 */
public class PicassoHolder extends BaseViewHolder<InstrumentedImageView> {

    private final Picasso mPicasso;

    public PicassoHolder(
            Context context, Picasso picasso, View parent,
            InstrumentedImageView view, PerfListener perfListener) {
        super(context, parent, view, perfListener);
        mPicasso = picasso;
    }

    @Override
    protected void onBind(String uri) {
        mPicasso.load(uri)
                .placeholder(Drawables.sPlaceholderDrawable)
                .error(Drawables.sErrorDrawable)
                .fit()
                .into(mImageView);
    }
}
