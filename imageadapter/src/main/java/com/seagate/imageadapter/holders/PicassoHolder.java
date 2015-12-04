
package com.seagate.imageadapter.holders;

import android.content.Context;
import android.view.View;

import com.seagate.imageadapter.Drawees;
import com.seagate.imageadapter.instrumentation.InstrumentedImageView;
import com.seagate.imageadapter.instrumentation.PerfListener;
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
                .placeholder(Drawees.sPlaceholderDrawable)
                .error(Drawees.sErrorDrawable)
                .fit()
                .into(mImageView);
    }
}
