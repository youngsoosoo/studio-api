package com.studio.api.portfolio.service;

import com.studio.api.common.NotFoundException;
import com.studio.api.portfolio.dto.AboutDto;
import com.studio.api.portfolio.dto.AchievementDto;
import com.studio.api.portfolio.dto.AwardDto;
import com.studio.api.portfolio.dto.CertificationDto;
import com.studio.api.portfolio.dto.EducationDto;
import com.studio.api.portfolio.dto.ExperienceDto;
import com.studio.api.portfolio.dto.ProfileDto;
import com.studio.api.portfolio.dto.ProjectDetailResponseDto;
import com.studio.api.portfolio.dto.ProjectSummaryDto;
import com.studio.api.portfolio.dto.StrengthDto;
import com.studio.api.portfolio.dto.TechStackGroupDto;
import com.studio.api.portfolio.dto.TimelineEntryDto;
import com.studio.api.portfolio.repository.AboutRepository;
import com.studio.api.portfolio.repository.AchievementRepository;
import com.studio.api.portfolio.repository.AwardRepository;
import com.studio.api.portfolio.repository.CertificationRepository;
import com.studio.api.portfolio.repository.EducationRepository;
import com.studio.api.portfolio.repository.ExperienceRepository;
import com.studio.api.portfolio.repository.ProfileRepository;
import com.studio.api.portfolio.repository.ProjectDetailRepository;
import com.studio.api.portfolio.repository.ProjectRepository;
import com.studio.api.portfolio.repository.StrengthRepository;
import com.studio.api.portfolio.repository.TechStackGroupRepository;
import com.studio.api.portfolio.repository.TimelineEntryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads one portfolio section per call, each in its own read-only transaction.
 * Separated from {@link DatabasePortfolioService} so the aggregate endpoint can
 * run these loads on different threads (each needs its own transaction/session);
 * a bean boundary means the {@code @Transactional} proxy applies per call rather
 * than being bypassed by self-invocation.
 */
@Component
@RequiredArgsConstructor
public class PortfolioSectionLoader {

    private static final long SINGLETON_ID = 1L;

    private final ProfileRepository profileRepository;
    private final AboutRepository aboutRepository;
    private final StrengthRepository strengthRepository;
    private final AchievementRepository achievementRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;
    private final ProjectDetailRepository projectDetailRepository;
    private final TechStackGroupRepository techStackGroupRepository;
    private final EducationRepository educationRepository;
    private final AwardRepository awardRepository;
    private final CertificationRepository certificationRepository;
    private final TimelineEntryRepository timelineEntryRepository;
    private final PortfolioMapper mapper;

    @Transactional(readOnly = true)
    public ProfileDto loadProfile() {
        return profileRepository.findById(SINGLETON_ID)
                .map(mapper::toProfile)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
    }

    @Transactional(readOnly = true)
    public AboutDto loadAbout() {
        return aboutRepository.findById(SINGLETON_ID)
                .map(mapper::toAbout)
                .orElseThrow(() -> new NotFoundException("About not found"));
    }

    @Transactional(readOnly = true)
    public List<StrengthDto> loadStrengths() {
        return strengthRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toStrength)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> loadAchievements() {
        return achievementRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toAchievement)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExperienceDto> loadExperiences() {
        return experienceRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toExperience)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProjectSummaryDto> loadProjects() {
        return projectRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toProjectSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TechStackGroupDto> loadTechStack() {
        return techStackGroupRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toTechStackGroup)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EducationDto> loadEducation() {
        return educationRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toEducation)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AwardDto> loadAwards() {
        return awardRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toAward)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CertificationDto> loadCertifications() {
        return certificationRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toCertification)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TimelineEntryDto> loadTimeline() {
        return timelineEntryRepository.findAllByOrderBySortOrderAsc().stream()
                .map(mapper::toTimelineEntry)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectDetailResponseDto loadProjectDetail(String slug) {
        return projectDetailRepository.findByProjectSlug(slug)
                .map(mapper::toProjectDetail)
                .orElseThrow(() -> new NotFoundException("Project detail not found: " + slug));
    }
}
