package com.studio.api.portfolio.controller;

import com.studio.api.common.ApiResponseDto;
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
import com.studio.api.portfolio.service.PortfolioReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Read-only public portfolio API.
 *
 * <p>{@code GET /api/portfolio} returns the whole page in one request and is the
 * endpoint the frontend consumes. The per-section endpoints expose the same data
 * for future detail pages / partial refreshes, and
 * {@code GET /api/portfolio/projects/{slug}} serves a project's case study for
 * the detail modal.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioReader portfolioReader;

    /** 프로필부터 타임라인까지 포트폴리오의 모든 섹션을 한 번에 조회한다. */
    @GetMapping
    public ApiResponseDto<PortfolioResponseDto> getPortfolio() {
        return ApiResponseDto.ok(portfolioReader.getPortfolio());
    }

    /** 이름, 직무, 소개 문구, 소셜 링크 등 기본 프로필 정보를 조회한다. */
    @GetMapping("/profile")
    public ApiResponseDto<ProfileDto> getProfile() {
        return ApiResponseDto.ok(portfolioReader.getProfile());
    }

    /** 자기소개 본문과 주요 하이라이트를 조회한다. */
    @GetMapping("/about")
    public ApiResponseDto<AboutDto> getAbout() {
        return ApiResponseDto.ok(portfolioReader.getAbout());
    }

    /** 개발자로서의 핵심 강점 목록을 노출 순서대로 조회한다. */
    @GetMapping("/strengths")
    public ApiResponseDto<List<StrengthDto>> getStrengths() {
        return ApiResponseDto.ok(portfolioReader.getStrengths());
    }

    /** 성능 개선과 운영 안정화 등 주요 성과 목록을 조회한다. */
    @GetMapping("/achievements")
    public ApiResponseDto<List<AchievementDto>> getAchievements() {
        return ApiResponseDto.ok(portfolioReader.getAchievements());
    }

    /** 회사별 경력, 담당 업무, 기술 스택 정보를 조회한다. */
    @GetMapping("/experiences")
    public ApiResponseDto<List<ExperienceDto>> getExperiences() {
        return ApiResponseDto.ok(portfolioReader.getExperiences());
    }

    /** 프로젝트 카드에 표시할 프로젝트 요약 목록을 조회한다. */
    @GetMapping("/projects")
    public ApiResponseDto<List<ProjectSummaryDto>> getProjects() {
        return ApiResponseDto.ok(portfolioReader.getProjects());
    }

    /** 프로젝트 슬러그로 문제, 접근 방식, 성과 등 상세 사례를 조회한다. */
    @GetMapping("/projects/{slug}")
    public ApiResponseDto<ProjectDetailResponseDto> getProjectDetail(@PathVariable String slug) {
        return ApiResponseDto.ok(portfolioReader.getProjectDetail(slug));
    }

    /** 카테고리별 기술 스택과 숙련도 정보를 조회한다. */
    @GetMapping("/tech-stack")
    public ApiResponseDto<List<TechStackGroupDto>> getTechStack() {
        return ApiResponseDto.ok(portfolioReader.getTechStack());
    }

    /** 학력 정보를 노출 순서대로 조회한다. */
    @GetMapping("/education")
    public ApiResponseDto<List<EducationDto>> getEducation() {
        return ApiResponseDto.ok(portfolioReader.getEducation());
    }

    /** 수상 경력 정보를 노출 순서대로 조회한다. */
    @GetMapping("/awards")
    public ApiResponseDto<List<AwardDto>> getAwards() {
        return ApiResponseDto.ok(portfolioReader.getAwards());
    }

    /** 자격증과 인증 정보를 노출 순서대로 조회한다. */
    @GetMapping("/certifications")
    public ApiResponseDto<List<CertificationDto>> getCertifications() {
        return ApiResponseDto.ok(portfolioReader.getCertifications());
    }

    /** 경력과 주요 이력을 시간순 타임라인 형태로 조회한다. */
    @GetMapping("/timeline")
    public ApiResponseDto<List<TimelineEntryDto>> getTimeline() {
        return ApiResponseDto.ok(portfolioReader.getTimeline());
    }
}
