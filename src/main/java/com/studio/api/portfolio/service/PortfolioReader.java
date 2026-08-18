package com.studio.api.portfolio.service;

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
import java.util.List;

/** Read side of the portfolio API, implemented by {@link DatabasePortfolioService}. */
public interface PortfolioReader {

    PortfolioResponseDto getPortfolio();

    ProfileDto getProfile();

    AboutDto getAbout();

    List<StrengthDto> getStrengths();

    List<AchievementDto> getAchievements();

    List<ExperienceDto> getExperiences();

    List<ProjectSummaryDto> getProjects();

    List<TechStackGroupDto> getTechStack();

    List<EducationDto> getEducation();

    List<AwardDto> getAwards();

    List<CertificationDto> getCertifications();

    /**
     * Full case study for the project with the given slug.
     *
     * @throws com.studio.api.common.NotFoundException when the project does not
     *         exist or has no case study
     */
    ProjectDetailResponseDto getProjectDetail(String slug);
}
