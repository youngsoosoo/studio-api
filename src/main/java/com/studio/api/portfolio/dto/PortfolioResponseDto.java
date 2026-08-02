package com.studio.api.portfolio.dto;

import java.util.List;

/**
 * Aggregated payload powering the entire public portfolio page in one request.
 */
public record PortfolioResponseDto(
        ProfileDto profile,
        AboutDto about,
        List<StrengthDto> strengths,
        List<AchievementDto> achievements,
        List<ExperienceDto> experiences,
        List<ProjectSummaryDto> projects,
        List<TechStackGroupDto> techStack,
        List<EducationDto> education,
        List<AwardDto> awards,
        List<CertificationDto> certifications,
        List<TimelineEntryDto> timeline
) {
}
