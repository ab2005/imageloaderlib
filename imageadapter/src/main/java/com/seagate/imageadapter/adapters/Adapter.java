
package com.seagate.imageadapter.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.seagate.imageadapter.Drawees;
import com.seagate.imageadapter.configs.imagepipeline.ImagePipelineConfigFactory;
import com.seagate.imageadapter.holders.BaseViewHolder;
import com.seagate.imageadapter.instrumentation.PerfListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for RecyclerView Adapters
 */
public abstract class Adapter extends RecyclerView.Adapter<BaseViewHolder<?>> {
    static public final int FRESCO = 0;
    static public final int FRESCO_OKHTTP = 1;
    static public final int GLIDE = 2;
    static public final int PICASSO = 3;
    static public final int UNIVERSAL_IMAGE_LIBRARY = 4;
    static public final int ANDROID_QUERY = 5;
    static public final int VOLLEY = 6;
    static public final int VOLLEY_DRAWEE = 7;


    public static void initializeWithDefaults(Context ctx) {
        Drawees.initializeWithDefaults(ctx);
    }

    public static Adapter buildAdapter(int type, Adapter.Delegate ad) {
        final Context ctx = ad.getContext();
        final PerfListener pl = ad.getPerformanceListener();

        switch (type) {
            case FRESCO:
                return new FrescoAdapter(ctx, pl,
                        ImagePipelineConfigFactory.getImagePipelineConfig(ctx), ad);
            case FRESCO_OKHTTP:
                return new FrescoAdapter(ctx, pl,
                        ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(ctx), ad);
            case GLIDE:
                return new GlideAdapter(ctx, pl, ad);
            case PICASSO:
                return new PicassoAdapter(ctx, pl, ad);
            case UNIVERSAL_IMAGE_LIBRARY:
                return new UilAdapter(ctx, pl, ad);
            case VOLLEY:
                return new VolleyAdapter(ctx, pl, ad);
            case VOLLEY_DRAWEE:
                return new VolleyDraweeAdapter(ctx, pl, ad);
            case ANDROID_QUERY:
                return new AQueryAdapter(ctx, pl, ad);
            default:
                throw new IllegalArgumentException("unknown type " + type);
        }
    }

    private final PerfListener mPerfListener;
    private final Context mContext;
    private final Delegate mDelegate;
    private final List<String> mModel;

    Adapter(final Context context, final PerfListener perfListener, Delegate delegate) {
        this.mContext = context;
        this.mPerfListener = perfListener;
        this.mModel = new LinkedList<String>();
        this.mDelegate = delegate;
    }

    /**
     * Add url item.
     * @param url
     */
    final public void addUrl(final String url) {
        mModel.add(url);
    }

    /**
     * Clear url items.
     */
    final public void clear() {
        mModel.clear();
    }

    /**
     * Release any resources and tear down the adapter.
     */
    public abstract void dispose();

    @Override
    final public void onBindViewHolder(BaseViewHolder<?> holder, int position) {
        String url = getItem(position);
        holder.bind(url);
        mDelegate.bind(holder.itemView, url);
    }

    @Override
    final public int getItemCount() {
        return mModel.size();
    }

    final protected PerfListener getPerfListener() {
        return mPerfListener;
    }

    final protected String getItem(final int position) {
        return mModel.get(position);
    }

    final protected Context getContext() {
        return mContext;
    }

    final protected Delegate getDelegate() {
        return mDelegate;
    }

    final protected View getInstrumentedView(View parent) {
        return parent.findViewById(mDelegate.getImageViewId());
    }

    /**
     *
     */
    public interface Delegate {

        Context getContext();

        PerfListener getPerformanceListener();

        View getHolderView(ViewGroup parent, int viewType);

        int getImageViewId();

        void bind(View itemView, String url);
    }
}
