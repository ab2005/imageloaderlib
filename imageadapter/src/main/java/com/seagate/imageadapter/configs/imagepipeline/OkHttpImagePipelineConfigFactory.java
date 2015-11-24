
package com.seagate.imageadapter.configs.imagepipeline;

import android.content.Context;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.okhttp.OkHttpClient;

/**
 * Factory for getting an {@link com.facebook.imagepipeline.core.ImagePipelineConfig} that uses
 */
public class OkHttpImagePipelineConfigFactory {

    public static ImagePipelineConfig.Builder newBuilder(Context context, OkHttpClient okHttpClient) {
        return ImagePipelineConfig.newBuilder(context).setNetworkFetcher(new OkHttpNetworkFetcher(okHttpClient));
    }
}
