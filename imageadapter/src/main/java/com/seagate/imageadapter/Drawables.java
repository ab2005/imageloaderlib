/*
 * Copyright (C) 2015 Seagate LLC
 */

package com.seagate.imageadapter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;


/**
 * Holds static drawables used in the app.
 * <p/>
 * <p> Using static set of drawables allows us to easily determine state of image request
 * by simply looking what kind of drawable is passed to image view.
 */
public class Drawables {
    public static Drawable sPlaceholderDrawable;
    public static Drawable sErrorDrawable;

    public static void init(final Resources resources) {
        if (sPlaceholderDrawable == null) {
            sPlaceholderDrawable = resources.getDrawable(R.color.image_placeholder);
        }

        if (sErrorDrawable == null) {
            sErrorDrawable = resources.getDrawable(R.color.image_error);
        }
    }
}
