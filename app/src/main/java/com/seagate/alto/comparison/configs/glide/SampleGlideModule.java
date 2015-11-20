
package com.seagate.alto.comparison.configs.glide;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.GlideModule;
import com.seagate.alto.comparison.configs.ConfigConstants;


/**
 * {@link GlideModule} implementation for the sample app.
 */
public class SampleGlideModule implements GlideModule {
  @Override
  public void applyOptions(final Context context, GlideBuilder builder) {
    builder.setDiskCache(
        new DiskCache.Factory() {
          @Override
          public DiskCache build() {
            return DiskLruCacheWrapper.get(
                Glide.getPhotoCacheDir(context),
                ConfigConstants.MAX_DISK_CACHE_SIZE);
          }
        });
    builder.setMemoryCache(new LruResourceCache(ConfigConstants.MAX_MEMORY_CACHE_SIZE));
  }

  @Override
  public void registerComponents(Context context, Glide glide) {
  }
}
