
package com.seagate.alto.comparison.urlsfetcher;

/**
 * Image sizes supported by Imgur. To download an image of particular size we have
 * to append appropriate letter after the id of required image but before extension name.
 * For example, if we want a "big square" version of "nice-image.jpeg", then
 * we should request "nice-imageb.jpeg".
 */
public enum ImageSize {
    ORIGINAL_IMAGE(""),
    SMALL_SQUARE("s"),
    BIG_SQUARE("b"),
    SMALL_THUMBNAIL("t"),
    MEDIUM_THUMBNAIL("m"),
    LARGE_THUMBNAIL("l"),
    HUGE_THUMBNAIL("h");

    public final String suffix;

    private ImageSize(final String suffix) {
        this.suffix = suffix;
    }
}
