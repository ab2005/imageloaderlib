// Copyright (c) 2015. Seagate Technology PLC. All rights reserved.

package com.seagate.alto;

// the Application class is the first Alto code to execute

import android.app.Application;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.seagate.alto.utils.LogUtils;

public class AltoApplication extends Application {

    private static final String TAG = LogUtils.makeTag(AltoApplication.class);

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        startFresco();
    }

    private void startFresco() {
//        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder()
//                .setBaseDirectoryName("SeagateCloud/ImageCache")
//                .setBaseDirectoryPath(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
//                .setMaxCacheSize(50000000)
//                .build();
//        DiskCacheConfig smallImageDiskCacheConfig = DiskCacheConfig.newBuilder()
//                .setBaseDirectoryName("SeagateCloud/SmallImageCache")
//                .setBaseDirectoryPath(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
//                .setMaxCacheSize(10000000)
//                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
//                .setMainDiskCacheConfig(diskCacheConfig)
//                .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
                .build();
        Fresco.initialize(this, config);
    }


}