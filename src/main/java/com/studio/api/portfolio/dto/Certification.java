package com.studio.api.portfolio.dto;

/**
 * A certification or completed course ("수료 및 자격증").
 *
 * @param issuer       issuing organization
 * @param date         free-form date label, e.g. "2022.08"
 * @param credentialId credential / license number (nullable)
 */
public record Certification(
        String id,
        String name,
        String issuer,
        String date,
        String credentialId
) {
}
