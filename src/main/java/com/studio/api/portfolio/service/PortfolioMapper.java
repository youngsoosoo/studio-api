package com.studio.api.portfolio.service;

import com.studio.api.image.service.ImageUrlResolver;
import com.studio.api.portfolio.dto.AboutDto;
import com.studio.api.portfolio.dto.AchievementDto;
import com.studio.api.portfolio.dto.AwardDto;
import com.studio.api.portfolio.dto.CertificationDto;
import com.studio.api.portfolio.dto.EducationDto;
import com.studio.api.portfolio.dto.ExperienceDto;
import com.studio.api.portfolio.dto.ProfileDto;
import com.studio.api.portfolio.dto.ProjectChallengeDto;
import com.studio.api.portfolio.dto.ProjectDetailResponseDto;
import com.studio.api.portfolio.dto.ProjectImageDto;
import com.studio.api.portfolio.dto.ProjectMetricDto;
import com.studio.api.portfolio.dto.ProjectProblemDto;
import com.studio.api.portfolio.dto.ProjectProblemCaseDto;
import com.studio.api.portfolio.dto.ProjectSummaryDto;
import com.studio.api.portfolio.dto.SocialLinkDto;
import com.studio.api.portfolio.dto.StrengthDto;
import com.studio.api.portfolio.dto.TechItemDto;
import com.studio.api.portfolio.dto.TechStackGroupDto;
import com.studio.api.portfolio.dto.TimelineEntryDto;
import com.studio.api.portfolio.entity.AboutEntity;
import com.studio.api.portfolio.entity.AchievementEntity;
import com.studio.api.portfolio.entity.AwardEntity;
import com.studio.api.portfolio.entity.CertificationEntity;
import com.studio.api.portfolio.entity.EducationEntity;
import com.studio.api.portfolio.entity.ExperienceEntity;
import com.studio.api.portfolio.entity.ProfileEntity;
import com.studio.api.portfolio.entity.ProjectDetailEntity;
import com.studio.api.portfolio.entity.ProjectEntity;
import com.studio.api.portfolio.entity.StrengthEntity;
import com.studio.api.portfolio.entity.TechStackGroupEntity;
import com.studio.api.portfolio.entity.TimelineEntryEntity;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Entity → DTO mapping. Slugs become the public {@code id} fields and image
 * references are resolved to absolute URLs, so entities never leak into the
 * JSON contract.
 */
@Component
@RequiredArgsConstructor
public class PortfolioMapper {

    private final ImageUrlResolver imageUrlResolver;

    public ProfileDto toProfile(ProfileEntity entity) {
        return new ProfileDto(
                entity.getName(),
                entity.getTitle(),
                entity.getTagline(),
                entity.getLocation(),
                imageUrlResolver.resolve(entity.getAvatar()),
                entity.getResumeUrl(),
                entity.getSocials().stream()
                        .map(s -> new SocialLinkDto(s.getLabel(), s.getUrl(), s.getIcon()))
                        .toList()
        );
    }

    public AboutDto toAbout(AboutEntity entity) {
        return new AboutDto(
                entity.getHeadline(),
                List.copyOf(entity.getParagraphs()),
                List.copyOf(entity.getHighlights())
        );
    }

    public StrengthDto toStrength(StrengthEntity entity) {
        return new StrengthDto(entity.getSlug(), entity.getTitle(), entity.getDescription(), entity.getIcon());
    }

    public AchievementDto toAchievement(AchievementEntity entity) {
        return new AchievementDto(entity.getSlug(), entity.getTitle(), entity.getDescription(), entity.getMetric());
    }

    public ExperienceDto toExperience(ExperienceEntity entity) {
        return new ExperienceDto(
                entity.getSlug(),
                entity.getCompany(),
                entity.getRole(),
                entity.getPeriod(),
                entity.getLocation(),
                entity.getSummary(),
                List.copyOf(entity.getAchievements()),
                List.copyOf(entity.getStack())
        );
    }

    public ProjectSummaryDto toProjectSummary(ProjectEntity entity) {
        return new ProjectSummaryDto(
                entity.getSlug(),
                entity.getTitle(),
                entity.getSummary(),
                entity.getRole(),
                List.copyOf(entity.getTags()),
                entity.getPeriod(),
                imageUrlResolver.resolve(entity.getThumbnail()),
                entity.getRepoUrl(),
                entity.getLiveUrl(),
                entity.isFeatured()
        );
    }

    public ProjectDetailResponseDto toProjectDetail(ProjectDetailEntity entity) {
        return new ProjectDetailResponseDto(
                entity.getProject().getSlug(),
                toProjectSummary(entity.getProject()),
                List.copyOf(entity.getOverview()),
                entity.getProblem(),
                entity.getProblems().stream()
                        .map(p -> new ProjectProblemDto(p.getTitle(), p.getDescription()))
                        .toList(),
                List.copyOf(entity.getApproach()),
                List.copyOf(entity.getContributions()),
                entity.getChallenges().stream()
                        .map(c -> new ProjectChallengeDto(c.getTitle(), c.getDescription()))
                        .toList(),
                List.copyOf(entity.getOutcomes()),
                entity.getMetrics().stream()
                        .map(m -> new ProjectMetricDto(m.getLabel(), m.getMetricValue()))
                        .toList(),
                List.copyOf(entity.getStack()),
                entity.getTeam(),
                entity.getImages().stream()
                        .map(i -> new ProjectImageDto(
                                imageUrlResolver.resolve(i.getImage()), i.getAlt(), i.getCaption()))
                        .toList(),
                entity.getProblems().stream()
                        .map(p -> new ProjectProblemCaseDto(
                                p.getTitle(),
                                p.getDescription(),
                                List.copyOf(p.getApproach()),
                                p.getChallenges().stream()
                                        .map(c -> new ProjectChallengeDto(c.getTitle(), c.getDescription()))
                                        .toList(),
                                List.copyOf(p.getOutcomes()),
                                p.getMetrics().stream()
                                        .map(m -> new ProjectMetricDto(m.getLabel(), m.getMetricValue()))
                                        .toList()))
                        .toList()
        );
    }

    public TechStackGroupDto toTechStackGroup(TechStackGroupEntity entity) {
        return new TechStackGroupDto(
                entity.getCategory(),
                entity.getItems().stream()
                        .map(i -> new TechItemDto(i.getName(), i.getIcon(), i.getLevel()))
                        .toList()
        );
    }

    public EducationDto toEducation(EducationEntity entity) {
        return new EducationDto(
                entity.getSlug(), entity.getSchool(), entity.getDegree(),
                entity.getPeriod(), entity.getDescription());
    }

    public AwardDto toAward(AwardEntity entity) {
        return new AwardDto(
                entity.getSlug(), entity.getTitle(), entity.getIssuer(),
                entity.getDateText(), entity.getDescription());
    }

    public CertificationDto toCertification(CertificationEntity entity) {
        return new CertificationDto(
                entity.getSlug(), entity.getName(), entity.getIssuer(),
                entity.getDateText(), entity.getCredentialId());
    }

    public TimelineEntryDto toTimelineEntry(TimelineEntryEntity entity) {
        return new TimelineEntryDto(
                entity.getSlug(), entity.getDateText(), entity.getTitle(),
                entity.getDescription(), entity.getEntryType());
    }
}
