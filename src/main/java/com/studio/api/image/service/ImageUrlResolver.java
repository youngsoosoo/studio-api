package com.studio.api.image.service;

import com.studio.api.image.entity.ImageEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Builds the absolute public URL for an uploaded image. Absolute (not
 * root-relative) so the frontend can render it directly regardless of which
 * origin serves the page.
 */
@Component
public class ImageUrlResolver {

    private final String publicBaseUrl;

    public ImageUrlResolver(@Value("${app.public-base-url}") String publicBaseUrl) {
        this.publicBaseUrl = publicBaseUrl.endsWith("/")
                ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
    }

    /** Returns the public URL for the image, or null when the image is null. */
    public String resolve(ImageEntity image) {
        if (image == null) {
            return null;
        }
        return publicBaseUrl + "/files/" + image.getStoredName();
    }
}
