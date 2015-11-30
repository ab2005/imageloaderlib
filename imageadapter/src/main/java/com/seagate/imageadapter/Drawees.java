/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.imageadapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import com.facebook.stetho.Stetho;


/**
 * <p> Holds static context for the library.
 * <p/>
 * <p> Using static set of drawables allows us to easily determine state of image request
 * by simply looking what kind of drawable is passed to image view.
 */
public class Drawees {
    public static Drawable sPlaceholderDrawable;
    public static Drawable sErrorDrawable;

    public static void initializeWithDefaults(Context ctx) {
        Stetho.initializeWithDefaults(ctx);
        init(ctx.getResources());
    }

    private static void init(final Resources resources) {
        if (sPlaceholderDrawable == null) {
            sPlaceholderDrawable = resources.getDrawable(R.color.image_placeholder);
        }

        if (sErrorDrawable == null) {
            sErrorDrawable = resources.getDrawable(R.color.image_error);
        }
    }


}
