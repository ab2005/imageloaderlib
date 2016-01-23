
/*
 * Copyright (c) 2015. Seagate Technology PLC. All rights reserved.
 */

package com.seagate.alto.provider.imagepipeline;

import android.content.Context;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.seagate.alto.provider.Provider;
import com.squareup.okhttp.OkHttpClient;

/**
 * Creates ImagePipeline configuration that uses {@link }OkHttpNetworkFetcher}
 * with OkHttp as a backend for {@link Provider} calls.
 */
public class OkHttpImagePipelineConfigFactory {
    public static ImagePipelineConfig.Builder newBuilder(Context context, OkHttpClient okHttpClient, Provider provider) {
        return ImagePipelineConfig
                .newBuilder(context)
                .setNetworkFetcher(new OkHttpNetworkFetcher(okHttpClient, provider));
    }
}
