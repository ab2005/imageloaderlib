
package com.seagate.alto.comparison.holders;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.seagate.alto.comparison.instrumentation.InstrumentedDraweeView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * This is the Holder class for the RecycleView to use with Volley and Drawee
 */
public class VolleyDraweeHolder extends BaseViewHolder<InstrumentedDraweeView> {

    public VolleyDraweeHolder(
            Context context,
            View parentView,
            InstrumentedDraweeView view, PerfListener perfListener) {
        super(context, parentView, view, perfListener);
    }

    @Override
    protected void onBind(String uri) {
        mImageView.setImageURI(Uri.parse(uri));
    }
}
