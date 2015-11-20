
package com.seagate.alto.comparison.holders;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.seagate.alto.comparison.instrumentation.InstrumentedDraweeView;
import com.seagate.alto.comparison.instrumentation.PerfListener;

/**
 * This is the Holder class for the RecycleView to use with Fresco
 */
public class FrescoHolder extends BaseViewHolder<InstrumentedDraweeView> {

    public FrescoHolder(
            Context context, View parentView,
            InstrumentedDraweeView intrumentedDraweeView, PerfListener perfListener) {
        super(context, parentView, intrumentedDraweeView, perfListener);
    }

    @Override
    protected void onBind(String uriString) {
        Uri uri = Uri.parse(uriString);
        ImageRequestBuilder imageRequestBuilder =
                ImageRequestBuilder.newBuilderWithSource(uri);
        if (UriUtil.isNetworkUri(uri)) {
            imageRequestBuilder.setProgressiveRenderingEnabled(true);
        } else {
            imageRequestBuilder.setResizeOptions(new ResizeOptions(
                    mImageView.getLayoutParams().width + 2,
                    mImageView.getLayoutParams().height));
        }
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequestBuilder.build())
                .setOldController(mImageView.getController())
                .setControllerListener(mImageView.getListener())
                .setAutoPlayAnimations(true)
                .build();
        mImageView.setController(draweeController);
    }

}
