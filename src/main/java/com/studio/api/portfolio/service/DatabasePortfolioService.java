package com.studio.api.portfolio.service;

import com.studio.api.config.AsyncConfig;
import com.studio.api.config.CacheConfig;
import com.studio.api.portfolio.dto.AboutDto;
import com.studio.api.portfolio.dto.AchievementDto;
import com.studio.api.portfolio.dto.AwardDto;
import com.studio.api.portfolio.dto.CertificationDto;
import com.studio.api.portfolio.dto.EducationDto;
import com.studio.api.portfolio.dto.ExperienceDto;
import com.studio.api.portfolio.dto.PortfolioResponseDto;
import com.studio.api.portfolio.dto.ProfileDto;
import com.studio.api.portfolio.dto.ProjectDetailResponseDto;
import com.studio.api.portfolio.dto.ProjectSummaryDto;
import com.studio.api.portfolio.dto.StrengthDto;
import com.studio.api.portfolio.dto.TechStackGroupDto;
import com.studio.api.portfolio.dto.TimelineEntryDto;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Serves portfolio content from PostgreSQL. The aggregate endpoint loads the
 * sections concurrently via {@link PortfolioSectionLoader} so their DB round
 * trips overlap; the result is then cached, so the parallel work is paid only
 * on a cold request.
 */
@Service
@RequiredArgsConstructor
public class DatabasePortfolioService implements PortfolioReader {

    private final PortfolioSectionLoader loader;

    @Qualifier(AsyncConfig.PORTFOLIO_EXECUTOR)
    private final Executor portfolioTaskExecutor;

    @Override
    @Cacheable(CacheConfig.PORTFOLIO_CACHE)
    public PortfolioResponseDto getPortfolio() {
        // Each section loads in its own thread + transaction, so the ~11 DB
        // round trips overlap instead of summing on a cold (uncached) request.
        CompletableFuture<ProfileDto> profile = async(loader::loadProfile);
        CompletableFuture<AboutDto> about = async(loader::loadAbout);
        CompletableFuture<List<StrengthDto>> strengths = async(loader::loadStrengths);
        CompletableFuture<List<AchievementDto>> achievements = async(loader::loadAchievements);
        CompletableFuture<List<ExperienceDto>> experiences = async(loader::loadExperiences);
        CompletableFuture<List<ProjectSummaryDto>> projects = async(loader::loadProjects);
        CompletableFuture<List<TechStackGroupDto>> techStack = async(loader::loadTechStack);
        CompletableFuture<List<EducationDto>> education = async(loader::loadEducation);
        CompletableFuture<List<AwardDto>> awards = async(loader::loadAwards);
        CompletableFuture<List<CertificationDto>> certifications = async(loader::loadCertifications);
        CompletableFuture<List<TimelineEntryDto>> timeline = async(loader::loadTimeline);

        try {
            CompletableFuture.allOf(profile, about, strengths, achievements, experiences,
                    projects, techStack, education, awards, certifications, timeline).join();
        } catch (CompletionException e) {
            // Surface the real cause (e.g. NotFoundException) instead of the wrapper.
            throw e.getCause() instanceof RuntimeException re ? re : e;
        }

        return new PortfolioResponseDto(
                profile.join(), about.join(), strengths.join(), achievements.join(),
                experiences.join(), projects.join(), techStack.join(), education.join(),
                awards.join(), certifications.join(), timeline.join());
    }

    private <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, portfolioTaskExecutor);
    }

    @Override
    public ProfileDto getProfile() {
        return loader.loadProfile();
    }

    @Override
    public AboutDto getAbout() {
        return loader.loadAbout();
    }

    @Override
    public List<StrengthDto> getStrengths() {
        return loader.loadStrengths();
    }

    @Override
    public List<AchievementDto> getAchievements() {
        return loader.loadAchievements();
    }

    @Override
    public List<ExperienceDto> getExperiences() {
        return loader.loadExperiences();
    }

    @Override
    public List<ProjectSummaryDto> getProjects() {
        return loader.loadProjects();
    }

    @Override
    public List<TechStackGroupDto> getTechStack() {
        return loader.loadTechStack();
    }

    @Override
    public List<EducationDto> getEducation() {
        return loader.loadEducation();
    }

    @Override
    public List<AwardDto> getAwards() {
        return loader.loadAwards();
    }

    @Override
    public List<CertificationDto> getCertifications() {
        return loader.loadCertifications();
    }

    @Override
    public List<TimelineEntryDto> getTimeline() {
        return loader.loadTimeline();
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.PROJECT_DETAILS_CACHE, key = "#slug")
    public ProjectDetailResponseDto getProjectDetail(String slug) {
        return loader.loadProjectDetail(slug);
    }
}
