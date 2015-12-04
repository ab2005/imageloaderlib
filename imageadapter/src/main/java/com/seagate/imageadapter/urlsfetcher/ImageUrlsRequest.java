
package com.seagate.imageadapter.urlsfetcher;

import com.seagate.imageadapter.instrumentation.Preconditions;

import java.util.Map;

/**
 * Encapsulates url and set of image types together with corresponding
 * resizing options.
 */
public class ImageUrlsRequest {
    final private String mEndpointUrl;
    Map<ImageFormat, ImageSize> mRequestedImageFormats;

    ImageUrlsRequest(final String endpointUrl, Map<ImageFormat, ImageSize> requestedTypes) {
        mEndpointUrl = Preconditions.checkNotNull(endpointUrl);
        mRequestedImageFormats = Preconditions.checkNotNull(requestedTypes);
    }

    public String getEndpointUrl() {
        return mEndpointUrl;
    }

    public ImageSize getImageSize(ImageFormat imageFormat) {
        return mRequestedImageFormats.get(imageFormat);
    }
}
