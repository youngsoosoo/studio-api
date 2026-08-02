package com.studio.api.portfolio.dto;

/**
 * A social/profile link rendered as an icon button.
 *
 * @param label human-readable label (e.g. "GitHub")
 * @param url   absolute URL
 * @param icon  short icon key the frontend maps to an SVG (e.g. "github")
 */
public record SocialLinkDto(String label, String url, String icon) {
}
