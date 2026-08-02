package com.studio.api.portfolio.service;

import com.studio.api.image.service.ImageUrlResolver;
import com.studio.api.portfolio.dto.About;
import com.studio.api.portfolio.dto.Achievement;
import com.studio.api.portfolio.dto.Award;
import com.studio.api.portfolio.dto.Certification;
import com.studio.api.portfolio.dto.Education;
import com.studio.api.portfolio.dto.Experience;
import com.studio.api.portfolio.dto.Profile;
import com.studio.api.portfolio.dto.ProjectChallenge;
import com.studio.api.portfolio.dto.ProjectDetailResponse;
import com.studio.api.portfolio.dto.ProjectImage;
import com.studio.api.portfolio.dto.ProjectMetric;
import com.studio.api.portfolio.dto.ProjectProblem;
import com.studio.api.portfolio.dto.ProjectProblemCase;
import com.studio.api.portfolio.dto.ProjectSummary;
import com.studio.api.portfolio.dto.SocialLink;
import com.studio.api.portfolio.dto.Strength;
import com.studio.api.portfolio.dto.TechItem;
import com.studio.api.portfolio.dto.TechStackGroup;
import com.studio.api.portfolio.dto.TimelineEntry;
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

    public Profile toProfile(ProfileEntity entity) {
        return new Profile(
                entity.getName(),
                entity.getTitle(),
                entity.getTagline(),
                entity.getLocation(),
                imageUrlResolver.resolve(entity.getAvatar()),
                entity.getResumeUrl(),
                entity.getSocials().stream()
                        .map(s -> new SocialLink(s.getLabel(), s.getUrl(), s.getIcon()))
                        .toList()
        );
    }

    public About toAbout(AboutEntity entity) {
        return new About(
                entity.getHeadline(),
                List.copyOf(entity.getParagraphs()),
                List.copyOf(entity.getHighlights())
        );
    }

    public Strength toStrength(StrengthEntity entity) {
        return new Strength(entity.getSlug(), entity.getTitle(), entity.getDescription(), entity.getIcon());
    }

    public Achievement toAchievement(AchievementEntity entity) {
        return new Achievement(entity.getSlug(), entity.getTitle(), entity.getDescription(), entity.getMetric());
    }

    public Experience toExperience(ExperienceEntity entity) {
        return new Experience(
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

    public ProjectSummary toProjectSummary(ProjectEntity entity) {
        return new ProjectSummary(
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

    public ProjectDetailResponse toProjectDetail(ProjectDetailEntity entity) {
        return new ProjectDetailResponse(
                entity.getProject().getSlug(),
                toProjectSummary(entity.getProject()),
                List.copyOf(entity.getOverview()),
                entity.getProblem(),
                entity.getProblems().stream()
                        .map(p -> new ProjectProblem(p.getTitle(), p.getDescription()))
                        .toList(),
                List.copyOf(entity.getApproach()),
                List.copyOf(entity.getContributions()),
                entity.getChallenges().stream()
                        .map(c -> new ProjectChallenge(c.getTitle(), c.getDescription()))
                        .toList(),
                List.copyOf(entity.getOutcomes()),
                entity.getMetrics().stream()
                        .map(m -> new ProjectMetric(m.getLabel(), m.getMetricValue()))
                        .toList(),
                List.copyOf(entity.getStack()),
                entity.getTeam(),
                entity.getImages().stream()
                        .map(i -> new ProjectImage(
                                imageUrlResolver.resolve(i.getImage()), i.getAlt(), i.getCaption()))
                        .toList(),
                entity.getProblems().stream()
                        .map(p -> new ProjectProblemCase(
                                p.getTitle(),
                                p.getDescription(),
                                List.copyOf(p.getApproach()),
                                p.getChallenges().stream()
                                        .map(c -> new ProjectChallenge(c.getTitle(), c.getDescription()))
                                        .toList(),
                                List.copyOf(p.getOutcomes()),
                                p.getMetrics().stream()
                                        .map(m -> new ProjectMetric(m.getLabel(), m.getMetricValue()))
                                        .toList()))
                        .toList()
        );
    }

    public TechStackGroup toTechStackGroup(TechStackGroupEntity entity) {
        return new TechStackGroup(
                entity.getCategory(),
                entity.getItems().stream()
                        .map(i -> new TechItem(i.getName(), i.getIcon(), i.getLevel()))
                        .toList()
        );
    }

    public Education toEducation(EducationEntity entity) {
        return new Education(
                entity.getSlug(), entity.getSchool(), entity.getDegree(),
                entity.getPeriod(), entity.getDescription());
    }

    public Award toAward(AwardEntity entity) {
        return new Award(
                entity.getSlug(), entity.getTitle(), entity.getIssuer(),
                entity.getDateText(), entity.getDescription());
    }

    public Certification toCertification(CertificationEntity entity) {
        return new Certification(
                entity.getSlug(), entity.getName(), entity.getIssuer(),
                entity.getDateText(), entity.getCredentialId());
    }

    public TimelineEntry toTimelineEntry(TimelineEntryEntity entity) {
        return new TimelineEntry(
                entity.getSlug(), entity.getDateText(), entity.getTitle(),
                entity.getDescription(), entity.getEntryType());
    }
}
