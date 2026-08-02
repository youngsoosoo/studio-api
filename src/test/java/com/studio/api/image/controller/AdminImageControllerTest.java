package com.studio.api.image.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studio.api.config.AdminKeyInterceptor;
import com.studio.api.portfolio.entity.ProfileEntity;
import com.studio.api.portfolio.repository.ProfileRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminImageControllerTest {

    private static final String ADMIN_KEY = "test-admin-key";
    private static final byte[] PNG_BYTES = {
            (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 1, 2, 3
    };

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Test
    void rejectsUploadWithoutKey() throws Exception {
        mockMvc.perform(multipart("/api/admin/images").file(pngFile()))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value("error"))
            .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void rejectsUploadWithWrongKey() throws Exception {
        mockMvc.perform(multipart("/api/admin/images").file(pngFile())
                .header(AdminKeyInterceptor.HEADER, "wrong-key"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void storesUploadedFileAndReturnsUrl() throws Exception {
        MvcResult result = mockMvc.perform(multipart("/api/admin/images").file(pngFile())
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.data.url").exists())
            .andExpect(jsonPath("$.data.originalName").value("me.png"))
            .andExpect(jsonPath("$.data.contentType").value("image/png"))
            .andReturn();

        JsonNode data = objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("data");
        String url = data.get("url").asText();
        assertThat(url).startsWith("http://localhost:8080/files/");
        String storedName = url.substring(url.lastIndexOf('/') + 1);
        assertThat(Files.exists(Path.of(uploadDir).resolve(storedName))).isTrue();
    }

    @Test
    void servesUploadedFileWithCorsForAllowedWebOrigins() throws Exception {
        MvcResult uploadResult = mockMvc.perform(multipart("/api/admin/images").file(pngFile())
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isCreated())
            .andReturn();

        String url = objectMapper
                .readTree(uploadResult.getResponse().getContentAsString())
                .get("data")
                .get("url")
                .asText();
        String filePath = url.substring(url.indexOf("/files/"));

        mockMvc.perform(get(filePath).header("Origin", "http://localhost:5173"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));

        mockMvc.perform(get(filePath).header("Origin", "http://localhost:4173"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4173"));

        mockMvc.perform(get(filePath).header("Origin", "https://studio-web-olive.vercel.app"))
            .andExpect(status().isOk())
            .andExpect(header().string(
                    "Access-Control-Allow-Origin",
                    "https://studio-web-olive.vercel.app"));
    }

    @Test
    void attachesAvatarToProfile() throws Exception {
        profileRepository.save(new ProfileEntity(
                1L,
                "Test User",
                "Backend Developer",
                "Synthetic test profile",
                "Test City",
                null));

        mockMvc.perform(multipart("/api/admin/images").file(pngFile())
                .param("target", "avatar")
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isCreated());

        ProfileEntity profile = profileRepository.findById(1L).orElseThrow();
        assertThat(profile.getAvatar()).isNotNull();
    }

    @Test
    void rejectsDisallowedFileType() throws Exception {
        MockMultipartFile txt = new MockMultipartFile(
                "file", "notes.txt", "text/plain", "hello".getBytes());
        mockMvc.perform(multipart("/api/admin/images").file(txt)
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsContentThatDoesNotMatchItsExtension() throws Exception {
        MockMultipartFile disguised = new MockMultipartFile(
                "file", "not-really.png", "image/png", "<html>unsafe</html>".getBytes());
        mockMvc.perform(multipart("/api/admin/images").file(disguised)
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsSvgUpload() throws Exception {
        MockMultipartFile svg = new MockMultipartFile(
                "file", "unsafe.svg", "image/svg+xml", "<svg><script/></svg>".getBytes());
        mockMvc.perform(multipart("/api/admin/images").file(svg)
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsThumbnailTargetWithoutProject() throws Exception {
        mockMvc.perform(multipart("/api/admin/images").file(pngFile())
                .param("target", "thumbnail")
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    @Test
    void rejectsUnknownProjectSlug() throws Exception {
        long filesBefore = uploadFileCount();

        mockMvc.perform(multipart("/api/admin/images").file(pngFile())
                .param("target", "thumbnail")
                .param("project", "nope")
                .header(AdminKeyInterceptor.HEADER, ADMIN_KEY))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));

        assertThat(uploadFileCount()).isEqualTo(filesBefore);
    }

    private MockMultipartFile pngFile() {
        return new MockMultipartFile("file", "me.png", "image/png", PNG_BYTES);
    }

    private long uploadFileCount() throws Exception {
        Path root = Path.of(uploadDir);
        if (!Files.exists(root)) {
            return 0;
        }
        try (var files = Files.list(root)) {
            return files.count();
        }
    }
}
