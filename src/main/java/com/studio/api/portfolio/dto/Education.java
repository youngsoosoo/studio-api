package com.studio.api.portfolio.dto;

/**
 * An education entry ("학력").
 *
 * @param period free-form range, e.g. "2017.03 - 2021.02"
 */
public record Education(
        String id,
        String school,
        String degree,
        String period,
        String description
) {
}
