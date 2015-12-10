package com.seagate.alto;

// add a class header comment here

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;
import com.seagate.alto.events.BusMaster;
import com.seagate.alto.events.ItemSelectedEvent;
import com.seagate.alto.utils.LogUtils;
import com.squareup.otto.Subscribe;

public class DetailView extends android.support.v4.widget.NestedScrollView {

    private static String TAG = LogUtils.makeTag(DetailView.class);

    private View mView;

    public DetailView(Context context) {
        this(context, null, 0);
    }

    public DetailView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    public DetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Log.d(TAG, "constructor");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d(TAG, "finalize");
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate");
        BusMaster.getBus().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow");
        BusMaster.getBus().unregister(this);
    }

    public void showItem(int index) {

        SimpleDraweeView sdv = (SimpleDraweeView) findViewById(R.id.image);
        if (sdv != null) {
            sdv.setImageURI(PlaceholderContent.getUri(index));
        }

    }
    
    @Subscribe
    public void answerAvailable(ItemSelectedEvent event) {
        // TODO: React to the event somehow!
        Log.d(TAG, "item selected: " + event.getPosition());

        showItem(event.getPosition());

    }

}
