
package com.seagate.alto.comparison.urlsfetcher;

/**
 * Formats of images we download from imgur.
 */
public enum ImageFormat {
    JPEG("image/jpeg"),
    PNG("image/png"),
    GIF("image/gif");

    private static final ImageFormat[] VALUES = values();

    public final String mime;

    private ImageFormat(final String mime) {
        this.mime = mime;
    }

    public static ImageFormat getImageFormatForMime(String mime) {
        for (ImageFormat type : VALUES) {
            if (type.mime.equals(mime)) {
                return type;
            }
        }
        return null;
    }
}
