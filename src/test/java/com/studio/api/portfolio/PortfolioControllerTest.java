package com.studio.api.portfolio;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.studio.api.common.NotFoundException;
import com.studio.api.portfolio.controller.PortfolioController;
import com.studio.api.portfolio.dto.AboutDto;
import com.studio.api.portfolio.dto.PortfolioResponseDto;
import com.studio.api.portfolio.dto.ProfileDto;
import com.studio.api.portfolio.dto.ProjectChallengeDto;
import com.studio.api.portfolio.dto.ProjectDetailResponseDto;
import com.studio.api.portfolio.dto.ProjectMetricDto;
import com.studio.api.portfolio.dto.ProjectProblemCaseDto;
import com.studio.api.portfolio.dto.ProjectSummaryDto;
import com.studio.api.portfolio.dto.ProjectVisualDto;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.studio.api.portfolio.service.PortfolioReader;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Controller slice test using synthetic responses. It verifies the public JSON
 * contract without connecting to a database or storing personal portfolio data.
 */
@WebMvcTest(PortfolioController.class)
class PortfolioControllerTest {

    private static final ProfileDto PROFILE = new ProfileDto(
            "Test User",
            "Backend Developer",
            "Synthetic test profile",
            "Test City",
            null,
            null,
            List.of());

    private static final AboutDto ABOUT = new AboutDto(
            "Test introduction",
            List.of("Synthetic paragraph"),
            List.of("Synthetic highlight"));

    private static final ProjectSummaryDto PROJECT = new ProjectSummaryDto(
            "sample-project",
            "Sample Project",
            "Synthetic project used only by tests",
            "Backend",
            List.of("Java", "Spring Boot"),
            "2026",
            null,
            null,
            null,
            true);

    private static final ProjectDetailResponseDto PROJECT_DETAIL = new ProjectDetailResponseDto(
            PROJECT.id(),
            PROJECT,
            List.of("Synthetic overview"),
            "Synthetic problem",
            List.of(),
            List.of("Synthetic approach"),
            List.of(),
            List.of(),
            List.of("Synthetic outcome"),
            List.of(),
            PROJECT.tags(),
            null,
            List.of(),
            List.of(
                    new ProjectProblemCaseDto(
                            "problem",
                            "Synthetic problem case",
                            "Synthetic problem definition",
                            List.of("Synthetic solution step"),
                            List.of(new ProjectChallengeDto(
                                    "Synthetic technical challenge",
                                    "Synthetic technical result")),
                            List.of("Synthetic case outcome"),
                            List.of(new ProjectMetricDto("Synthetic metric", "42%")),
                            List.of(),
                            List.of(new ProjectVisualDto(
                                    1L,
                                    "flow",
                                    "Synthetic flow",
                                    null,
                                    null,
                                    null,
                                    JsonNodeFactory.instance.objectNode()
                                            .put("layout", "vertical"),
                                    1,
                                    0))),
                    new ProjectProblemCaseDto(
                            "feature",
                            "Synthetic feature case",
                            "Synthetic feature description",
                            List.of("Synthetic feature step"),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of())));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PortfolioReader portfolioReader;

    @BeforeEach
    void setUpReader() {
        when(portfolioReader.getPortfolio()).thenReturn(new PortfolioResponseDto(
                PROFILE,
                ABOUT,
                List.of(),
                List.of(),
                List.of(),
                List.of(PROJECT),
                List.of(),
                List.of(),
                List.of(),
                List.of()));
        when(portfolioReader.getProjects()).thenReturn(List.of(PROJECT));
        when(portfolioReader.getProjectDetail(PROJECT.id())).thenReturn(PROJECT_DETAIL);
        when(portfolioReader.getProjectDetail("missing"))
                .thenThrow(new NotFoundException("Project detail not found: missing"));
    }

    @Test
    void returnsAggregatedPortfolio() throws Exception {
        mockMvc.perform(get("/api/portfolio")
                .header("Origin", "https://studio-bu0uukfi6-studio-198a.vercel.app"))
            .andExpect(status().isOk())
            .andExpect(header().string(
                    "Access-Control-Allow-Origin",
                    "https://studio-bu0uukfi6-studio-198a.vercel.app"))
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.error").doesNotExist())
            .andExpect(jsonPath("$.data.profile.name").value("Test User"))
            .andExpect(jsonPath("$.data.projects.length()").value(1))
            .andExpect(jsonPath("$.data.projects[0].id").value("sample-project"))
            .andExpect(jsonPath("$.data.techStack").isArray())
            .andExpect(jsonPath("$.data.achievements").isArray())
            .andExpect(jsonPath("$.data.education").isArray())
            .andExpect(jsonPath("$.data.awards").isArray())
            .andExpect(jsonPath("$.data.certifications").isArray());
    }

    @Test
    void returnsProjectsSection() throws Exception {
        mockMvc.perform(get("/api/portfolio/projects"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].id").value("sample-project"))
            .andExpect(jsonPath("$.data[0].tags").isArray());
    }

    @Test
    void returnsProjectDetail() throws Exception {
        mockMvc.perform(get("/api/portfolio/projects/sample-project"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.id").value("sample-project"))
            .andExpect(jsonPath("$.data.project.title").value("Sample Project"))
            .andExpect(jsonPath("$.data.overview.length()").value(1))
            .andExpect(jsonPath("$.data.approach.length()").value(1))
            .andExpect(jsonPath("$.data.stack.length()").value(2))
            .andExpect(jsonPath("$.data.images").isArray())
            .andExpect(jsonPath("$.data.problemCases.length()").value(2))
            .andExpect(jsonPath("$.data.problemCases[0].kind").value("problem"))
            .andExpect(jsonPath("$.data.problemCases[0].title").value("Synthetic problem case"))
            .andExpect(jsonPath("$.data.problemCases[0].approach.length()").value(1))
            .andExpect(jsonPath("$.data.problemCases[0].challenges.length()").value(1))
            .andExpect(jsonPath("$.data.problemCases[0].metrics[0].value").value("42%"))
            .andExpect(jsonPath("$.data.problemCases[0].images").isArray())
            .andExpect(jsonPath("$.data.problemCases[0].visuals.length()").value(1))
            .andExpect(jsonPath("$.data.problemCases[0].visuals[0].type").value("flow"))
            .andExpect(jsonPath("$.data.problemCases[0].visuals[0].payload.layout")
                    .value("vertical"))
            .andExpect(jsonPath("$.data.problemCases[1].kind").value("feature"))
            .andExpect(jsonPath("$.data.problemCases[1].title").value("Synthetic feature case"))
            .andExpect(jsonPath("$.data.problemCases[1].metrics.length()").value(0));
    }

    @Test
    void returnsNotFoundForUnknownProjectDetail() throws Exception {
        mockMvc.perform(get("/api/portfolio/projects/missing"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.error.code").value("NOT_FOUND"))
            .andExpect(jsonPath("$.data").doesNotExist());
    }
}
