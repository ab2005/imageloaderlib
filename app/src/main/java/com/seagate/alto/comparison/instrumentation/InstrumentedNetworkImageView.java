
package com.seagate.alto.comparison.instrumentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.android.volley.toolbox.NetworkImageView;
import com.seagate.alto.R;

/**
 * {@link NetworkImageView} that notifies its instance of {@link Instrumentation} whenever an image
 * request lifecycle event happens.
 */
public class InstrumentedNetworkImageView extends NetworkImageView implements Instrumented {

    private final Instrumentation mInstrumentation;

    public InstrumentedNetworkImageView(final Context context) {
        super(context);
        mInstrumentation = new Instrumentation(this);
    }

    @Override
    public void initInstrumentation(final String tag, final PerfListener perfListener) {
        mInstrumentation.init(tag, perfListener);
        // we don't have a better estimate on when to call onStart, so do it here.
        mInstrumentation.onStart();
    }

    @Override
    public void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mInstrumentation.onDraw(canvas);
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        // bm == null in couple of situations like
        // - detaching from window
        // - cleaning up previous request
        if (bm != null) {
            mInstrumentation.onSuccess(0);
        }
        super.setImageBitmap(bm);
    }

    public void setImageResource(int resourceId) {
        if (resourceId == R.color.image_placeholder) {
            // ignore
        } else if (resourceId == R.color.image_error) {
            mInstrumentation.onFailure();
        } else {
            throw new IllegalArgumentException("Unrecognized resourceId");
        }
        super.setImageResource(resourceId);
    }
}
