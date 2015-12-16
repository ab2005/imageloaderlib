// Copyright (c) 2015. Seagate Technology PLC. All rights reserved.

package com.seagate.alto;

// random, stable content

import android.net.Uri;
import android.util.SparseArray;

import java.util.Random;

public class PlaceholderContent {

    private static Random random = new Random();

    public static String INDEX = "index";

    private static SparseArray<Uri> uris = new SparseArray<>();

    public static int getCount() { return 100; }

    public static Uri getUri(int position) {

        Uri uri = uris.get(position);

        if (uri == null) {

            int height = 300 + random.nextInt(10);
            int width = 300 + random.nextInt(10);

            String fun = "http://fillmurray.com/" + width + "/" + height;

            uri = Uri.parse(fun);

            uris.put(position, uri);

        }

        return uri;

    }
}