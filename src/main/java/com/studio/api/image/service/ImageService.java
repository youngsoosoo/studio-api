package com.studio.api.image.service;

import com.studio.api.common.NotFoundException;
import com.studio.api.config.CacheConfig;
import com.studio.api.image.dto.ImageResponseDto;
import com.studio.api.image.entity.ImageEntity;
import com.studio.api.image.repository.ImageRepository;
import com.studio.api.portfolio.entity.ProjectDetailEntity;
import com.studio.api.portfolio.entity.ProjectEntity;
import com.studio.api.portfolio.entity.ProjectImageEntity;
import com.studio.api.portfolio.entity.ProjectProblemEntity;
import com.studio.api.portfolio.entity.ProjectProblemKind;
import com.studio.api.portfolio.entity.ProjectProblemVisualEntity;
import com.studio.api.portfolio.repository.ProfileRepository;
import com.studio.api.portfolio.repository.ProjectDetailRepository;
import com.studio.api.portfolio.repository.ProjectImageRepository;
import com.studio.api.portfolio.repository.ProjectProblemRepository;
import com.studio.api.portfolio.repository.ProjectProblemVisualRepository;
import com.studio.api.portfolio.repository.ProjectRepository;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

/**
 * Stores an uploaded image and, optionally, attaches it to its target in the
 * same transaction so a single request is enough to make a photo live.
 * Replacing an avatar/thumbnail only repoints the FK; the previous file and
 * image row are left orphaned (cleanup is future work).
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    public static final String TARGET_AVATAR = "avatar";
    public static final String TARGET_THUMBNAIL = "thumbnail";
    public static final String TARGET_PROJECT_IMAGE = "project-image";
    public static final String TARGET_PROBLEM_VISUAL_IMAGE = "problem-visual-image";

    private static final long PROFILE_SINGLETON_ID = 1L;

    private final FileStorageService fileStorageService;
    private final ImageRepository imageRepository;
    private final ProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final ProjectDetailRepository projectDetailRepository;
    private final ProjectImageRepository projectImageRepository;
    private final ProjectProblemRepository projectProblemRepository;
    private final ProjectProblemVisualRepository projectProblemVisualRepository;
    private final ImageUrlResolver imageUrlResolver;

    @Transactional
    @CacheEvict(cacheNames = {CacheConfig.PORTFOLIO_CACHE, CacheConfig.PROJECT_DETAILS_CACHE}, allEntries = true)
    public ImageResponseDto upload(
            MultipartFile file,
            String target,
            String projectSlug,
            String alt,
            String caption,
            String title,
            String problemKind,
            Integer problemOrder,
            Integer visualOrder) {
        validateTarget(target, projectSlug, problemKind, problemOrder, visualOrder);
        String storedName = fileStorageService.store(file);
        registerRollbackCleanup(storedName);
        ImageEntity image = imageRepository.save(new ImageEntity(
                storedName,
                file.getOriginalFilename() == null ? storedName : file.getOriginalFilename(),
                file.getContentType(),
                file.getSize()
        ));
        if (target != null && !target.isBlank()) {
            attach(
                    image,
                    target,
                    projectSlug,
                    alt,
                    caption,
                    title,
                    problemKind,
                    problemOrder,
                    visualOrder);
        }
        return new ImageResponseDto(
                image.getId(),
                imageUrlResolver.resolve(image),
                image.getOriginalName(),
                image.getContentType(),
                image.getSizeBytes()
        );
    }

    private void validateTarget(
            String target,
            String projectSlug,
            String problemKind,
            Integer problemOrder,
            Integer visualOrder) {
        if (target == null || target.isBlank()) {
            return;
        }
        switch (target) {
            case TARGET_AVATAR -> profileRepository.findById(PROFILE_SINGLETON_ID)
                    .orElseThrow(() -> new NotFoundException("Profile not found"));
            case TARGET_THUMBNAIL -> requireProject(projectSlug);
            case TARGET_PROJECT_IMAGE -> projectDetailRepository
                    .findByProjectSlug(requireSlug(projectSlug))
                    .orElseThrow(() -> new NotFoundException("Project detail not found: " + projectSlug));
            case TARGET_PROBLEM_VISUAL_IMAGE -> {
                requireProblem(projectSlug, problemKind, problemOrder);
                if (visualOrder == null || visualOrder < 0) {
                    throw new IllegalArgumentException(
                            "visualOrder must be zero or greater for problem-visual-image");
                }
            }
            default -> throw new IllegalArgumentException("Unknown target: " + target);
        }
    }

    private void registerRollbackCleanup(String storedName) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            fileStorageService.deleteQuietly(storedName);
            throw new IllegalStateException("Image upload requires an active transaction");
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != TransactionSynchronization.STATUS_COMMITTED) {
                    fileStorageService.deleteQuietly(storedName);
                }
            }
        });
    }

    private void attach(
            ImageEntity image,
            String target,
            String projectSlug,
            String alt,
            String caption,
            String title,
            String problemKind,
            Integer problemOrder,
            Integer visualOrder) {
        switch (target) {
            case TARGET_AVATAR -> profileRepository.findById(PROFILE_SINGLETON_ID)
                    .orElseThrow(() -> new NotFoundException("Profile not found"))
                    .setAvatar(image);
            case TARGET_THUMBNAIL -> requireProject(projectSlug).setThumbnail(image);
            case TARGET_PROJECT_IMAGE -> {
                ProjectDetailEntity detail = projectDetailRepository
                        .findByProjectSlug(requireSlug(projectSlug))
                        .orElseThrow(() -> new NotFoundException("Project detail not found: " + projectSlug));
                String effectiveAlt = (alt == null || alt.isBlank()) ? image.getOriginalName() : alt;
                projectImageRepository.save(new ProjectImageEntity(
                        detail, image, effectiveAlt, caption, detail.getImages().size()));
            }
            case TARGET_PROBLEM_VISUAL_IMAGE -> {
                ProjectProblemEntity problem = requireProblem(projectSlug, problemKind, problemOrder);
                String effectiveAlt = (alt == null || alt.isBlank()) ? image.getOriginalName() : alt;
                projectProblemVisualRepository
                        .findByProblemIdAndSortOrder(problem.getId(), visualOrder)
                        .ifPresent(projectProblemVisualRepository::delete);
                projectProblemVisualRepository.flush();
                projectProblemVisualRepository.save(ProjectProblemVisualEntity.image(
                        problem,
                        image,
                        title,
                        effectiveAlt,
                        caption,
                        visualOrder));
            }
            default -> throw new IllegalArgumentException("Unknown target: " + target);
        }
    }

    private ProjectProblemEntity requireProblem(
            String projectSlug, String problemKind, Integer problemOrder) {
        if (problemKind == null || problemKind.isBlank()) {
            throw new IllegalArgumentException(
                    "problemKind is required for problem-visual-image");
        }
        if (problemOrder == null || problemOrder < 0) {
            throw new IllegalArgumentException(
                    "problemOrder must be zero or greater for problem-visual-image");
        }

        ProjectProblemKind kind;
        try {
            kind = ProjectProblemKind.valueOf(problemKind.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("problemKind must be problem or feature", exception);
        }

        return projectProblemRepository
                .findCase(requireSlug(projectSlug), kind, problemOrder)
                .orElseThrow(() -> new NotFoundException(
                        "Project case not found: " + projectSlug + "/" + problemKind + "/" + problemOrder));
    }

    private ProjectEntity requireProject(String projectSlug) {
        return projectRepository.findBySlug(requireSlug(projectSlug))
                .orElseThrow(() -> new NotFoundException("Project not found: " + projectSlug));
    }

    private String requireSlug(String projectSlug) {
        if (projectSlug == null || projectSlug.isBlank()) {
            throw new IllegalArgumentException("project parameter is required for this target");
        }
        return projectSlug;
    }
}
