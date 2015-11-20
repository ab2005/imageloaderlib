
package com.seagate.alto.comparison.configs.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Default bitmap memory cache for Volley.
 */
public class VolleyMemoryCache implements ImageLoader.ImageCache {
    private final LruCache<String, Bitmap> mLruCache;

    public VolleyMemoryCache(int maxSize) {
        mLruCache = new LruCache<String, Bitmap>(maxSize) {
            protected int sizeOf(final String key, final Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mLruCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mLruCache.put(url, bitmap);
    }

    public void clear() {
        mLruCache.evictAll();
    }
}
