package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * Identity / hero section content.
 */
public record Profile(
        String name,
        String title,
        String tagline,
        String location,
        String avatarUrl,
        String resumeUrl,
        List<SocialLink> socials
) {
}
