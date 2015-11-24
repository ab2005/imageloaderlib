
package com.seagate.imageadapter.configs.uil;

import android.content.Context;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.seagate.imageadapter.Drawables;
import com.seagate.imageadapter.configs.ConfigConstants;


/**
 * Provides instance of ImageLoader with appropriately configured caches and placeholder/failure
 * drawables.
 */
public class SampleUilFactory {
  private static ImageLoader sImageLoader;

  public static ImageLoader getImageLoader(Context context) {
    if (sImageLoader == null) {
      DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
          .showImageOnLoading(Drawables.sPlaceholderDrawable)
          .showImageOnFail(Drawables.sErrorDrawable)
          .cacheInMemory(true)
          .cacheOnDisk(true)
          .build();
      ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
          .defaultDisplayImageOptions(displayImageOptions)
          .diskCacheSize(ConfigConstants.MAX_DISK_CACHE_SIZE)
          .memoryCacheSize(ConfigConstants.MAX_MEMORY_CACHE_SIZE)
          .build();
      sImageLoader = ImageLoader.getInstance();
      sImageLoader.init(config);
    }
    return sImageLoader;
  }
}
