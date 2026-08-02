package com.studio.api.portfolio.service;

import com.studio.api.config.AsyncConfig;
import com.studio.api.config.CacheConfig;
import com.studio.api.portfolio.dto.About;
import com.studio.api.portfolio.dto.Achievement;
import com.studio.api.portfolio.dto.Award;
import com.studio.api.portfolio.dto.Certification;
import com.studio.api.portfolio.dto.Education;
import com.studio.api.portfolio.dto.Experience;
import com.studio.api.portfolio.dto.PortfolioResponse;
import com.studio.api.portfolio.dto.Profile;
import com.studio.api.portfolio.dto.ProjectDetailResponse;
import com.studio.api.portfolio.dto.ProjectSummary;
import com.studio.api.portfolio.dto.Strength;
import com.studio.api.portfolio.dto.TechStackGroup;
import com.studio.api.portfolio.dto.TimelineEntry;
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
    public PortfolioResponse getPortfolio() {
        // Each section loads in its own thread + transaction, so the ~11 DB
        // round trips overlap instead of summing on a cold (uncached) request.
        CompletableFuture<Profile> profile = async(loader::loadProfile);
        CompletableFuture<About> about = async(loader::loadAbout);
        CompletableFuture<List<Strength>> strengths = async(loader::loadStrengths);
        CompletableFuture<List<Achievement>> achievements = async(loader::loadAchievements);
        CompletableFuture<List<Experience>> experiences = async(loader::loadExperiences);
        CompletableFuture<List<ProjectSummary>> projects = async(loader::loadProjects);
        CompletableFuture<List<TechStackGroup>> techStack = async(loader::loadTechStack);
        CompletableFuture<List<Education>> education = async(loader::loadEducation);
        CompletableFuture<List<Award>> awards = async(loader::loadAwards);
        CompletableFuture<List<Certification>> certifications = async(loader::loadCertifications);
        CompletableFuture<List<TimelineEntry>> timeline = async(loader::loadTimeline);

        try {
            CompletableFuture.allOf(profile, about, strengths, achievements, experiences,
                    projects, techStack, education, awards, certifications, timeline).join();
        } catch (CompletionException e) {
            // Surface the real cause (e.g. NotFoundException) instead of the wrapper.
            throw e.getCause() instanceof RuntimeException re ? re : e;
        }

        return new PortfolioResponse(
                profile.join(), about.join(), strengths.join(), achievements.join(),
                experiences.join(), projects.join(), techStack.join(), education.join(),
                awards.join(), certifications.join(), timeline.join());
    }

    private <T> CompletableFuture<T> async(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, portfolioTaskExecutor);
    }

    @Override
    public Profile getProfile() {
        return loader.loadProfile();
    }

    @Override
    public About getAbout() {
        return loader.loadAbout();
    }

    @Override
    public List<Strength> getStrengths() {
        return loader.loadStrengths();
    }

    @Override
    public List<Achievement> getAchievements() {
        return loader.loadAchievements();
    }

    @Override
    public List<Experience> getExperiences() {
        return loader.loadExperiences();
    }

    @Override
    public List<ProjectSummary> getProjects() {
        return loader.loadProjects();
    }

    @Override
    public List<TechStackGroup> getTechStack() {
        return loader.loadTechStack();
    }

    @Override
    public List<Education> getEducation() {
        return loader.loadEducation();
    }

    @Override
    public List<Award> getAwards() {
        return loader.loadAwards();
    }

    @Override
    public List<Certification> getCertifications() {
        return loader.loadCertifications();
    }

    @Override
    public List<TimelineEntry> getTimeline() {
        return loader.loadTimeline();
    }

    @Override
    @Cacheable(cacheNames = CacheConfig.PROJECT_DETAILS_CACHE, key = "#slug")
    public ProjectDetailResponse getProjectDetail(String slug) {
        return loader.loadProjectDetail(slug);
    }
}
