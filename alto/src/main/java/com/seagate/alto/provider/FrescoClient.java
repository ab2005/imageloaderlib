/*
 * Copyright (c) 2015. Seagate Technology PLC. All rights reserved.
 */

package com.seagate.alto.provider;

import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.seagate.alto.provider.imagepipeline.ImagePipelineConfigFactory;

/**
 * Singleton instance of Fresco pre-configured
 */
public class FrescoClient {
    public static void init(Context context, Provider provider) {
        ImagePipelineConfig config = ImagePipelineConfigFactory.getOkHttpImagePipelineConfig(context, provider);
        Fresco.initialize(context, config);
    }
}
