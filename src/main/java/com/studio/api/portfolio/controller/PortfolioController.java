package com.studio.api.portfolio.controller;

import com.studio.api.common.ApiResponse;
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
    public ApiResponse<PortfolioResponse> getPortfolio() {
        return ApiResponse.ok(portfolioReader.getPortfolio());
    }

    /** 이름, 직무, 소개 문구, 소셜 링크 등 기본 프로필 정보를 조회한다. */
    @GetMapping("/profile")
    public ApiResponse<Profile> getProfile() {
        return ApiResponse.ok(portfolioReader.getProfile());
    }

    /** 자기소개 본문과 주요 하이라이트를 조회한다. */
    @GetMapping("/about")
    public ApiResponse<About> getAbout() {
        return ApiResponse.ok(portfolioReader.getAbout());
    }

    /** 개발자로서의 핵심 강점 목록을 노출 순서대로 조회한다. */
    @GetMapping("/strengths")
    public ApiResponse<List<Strength>> getStrengths() {
        return ApiResponse.ok(portfolioReader.getStrengths());
    }

    /** 성능 개선과 운영 안정화 등 주요 성과 목록을 조회한다. */
    @GetMapping("/achievements")
    public ApiResponse<List<Achievement>> getAchievements() {
        return ApiResponse.ok(portfolioReader.getAchievements());
    }

    /** 회사별 경력, 담당 업무, 기술 스택 정보를 조회한다. */
    @GetMapping("/experiences")
    public ApiResponse<List<Experience>> getExperiences() {
        return ApiResponse.ok(portfolioReader.getExperiences());
    }

    /** 프로젝트 카드에 표시할 프로젝트 요약 목록을 조회한다. */
    @GetMapping("/projects")
    public ApiResponse<List<ProjectSummary>> getProjects() {
        return ApiResponse.ok(portfolioReader.getProjects());
    }

    /** 프로젝트 슬러그로 문제, 접근 방식, 성과 등 상세 사례를 조회한다. */
    @GetMapping("/projects/{slug}")
    public ApiResponse<ProjectDetailResponse> getProjectDetail(@PathVariable String slug) {
        return ApiResponse.ok(portfolioReader.getProjectDetail(slug));
    }

    /** 카테고리별 기술 스택과 숙련도 정보를 조회한다. */
    @GetMapping("/tech-stack")
    public ApiResponse<List<TechStackGroup>> getTechStack() {
        return ApiResponse.ok(portfolioReader.getTechStack());
    }

    /** 학력 정보를 노출 순서대로 조회한다. */
    @GetMapping("/education")
    public ApiResponse<List<Education>> getEducation() {
        return ApiResponse.ok(portfolioReader.getEducation());
    }

    /** 수상 경력 정보를 노출 순서대로 조회한다. */
    @GetMapping("/awards")
    public ApiResponse<List<Award>> getAwards() {
        return ApiResponse.ok(portfolioReader.getAwards());
    }

    /** 자격증과 인증 정보를 노출 순서대로 조회한다. */
    @GetMapping("/certifications")
    public ApiResponse<List<Certification>> getCertifications() {
        return ApiResponse.ok(portfolioReader.getCertifications());
    }

    /** 경력과 주요 이력을 시간순 타임라인 형태로 조회한다. */
    @GetMapping("/timeline")
    public ApiResponse<List<TimelineEntry>> getTimeline() {
        return ApiResponse.ok(portfolioReader.getTimeline());
    }
}
