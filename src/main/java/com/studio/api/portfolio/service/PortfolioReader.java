package com.studio.api.portfolio.service;

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

/** Read side of the portfolio API, implemented by {@link DatabasePortfolioService}. */
public interface PortfolioReader {

    PortfolioResponse getPortfolio();

    Profile getProfile();

    About getAbout();

    List<Strength> getStrengths();

    List<Achievement> getAchievements();

    List<Experience> getExperiences();

    List<ProjectSummary> getProjects();

    List<TechStackGroup> getTechStack();

    List<Education> getEducation();

    List<Award> getAwards();

    List<Certification> getCertifications();

    List<TimelineEntry> getTimeline();

    /**
     * Full case study for the project with the given slug.
     *
     * @throws com.studio.api.common.NotFoundException when the project does not
     *         exist or has no case study
     */
    ProjectDetailResponse getProjectDetail(String slug);
}
