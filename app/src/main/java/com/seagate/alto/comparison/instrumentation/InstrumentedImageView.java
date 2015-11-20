
package com.seagate.alto.comparison.instrumentation;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.seagate.alto.comparison.Drawables;


/**
 * {@link ImageView} that notifies its instance of {@link Instrumentation} whenever an image request
 * lifecycle event happens.
 * <p/>
 * <p> setImageResource and setImageURI methods are not expected to be used by any library,
 * UnsupportedOperationException is thrown if those are called
 */
public class InstrumentedImageView extends ImageView implements Instrumented {

    private final Instrumentation mInstrumentation;

    public InstrumentedImageView(final Context context) {
        super(context);
        mInstrumentation = new Instrumentation(this);
    }

    public InstrumentedImageView(final Context ctx, @Nullable AttributeSet attrs) {
        super(ctx, attrs);
        mInstrumentation = new Instrumentation(this);
    }

    public InstrumentedImageView(final Context ctx, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(ctx, attrs, defStyleAttr);
        mInstrumentation = new Instrumentation(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InstrumentedImageView(final Context ctx, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(ctx, attrs, defStyleAttr, defStyleRes);
        mInstrumentation = new Instrumentation(this);
    }

    @Override
    public void initInstrumentation(final String tag, PerfListener perfListener) {
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
    public void setImageDrawable(final Drawable drawable) {
        if (drawable == null) {// AQuery preset drawable to be null if not found in cache
            return;
        }
        if (drawable == Drawables.sPlaceholderDrawable) {
            // ignore
        } else if (drawable == Drawables.sErrorDrawable) {
            mInstrumentation.onFailure();
        } else {
            // TODO: measure drawable size
            int h = drawable.getIntrinsicHeight();
            int w = drawable.getIntrinsicWidth();
            int size = w * h;
            mInstrumentation.onSuccess(size);
        }
        super.setImageDrawable(drawable);
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public void setImageResource(int resourceId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Throws UnsupportedOperationException
     */
    @Override
    public void setImageURI(Uri uri) {
        throw new UnsupportedOperationException();
    }
}
