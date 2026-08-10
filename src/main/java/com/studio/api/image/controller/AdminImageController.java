package com.studio.api.image.controller;

import com.studio.api.common.ApiResponseDto;
import com.studio.api.image.dto.ImageResponseDto;
import com.studio.api.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin-only image upload, guarded by the X-Admin-Key header (see
 * AdminKeyInterceptor). Meant to be called from curl/Postman — there is no
 * admin UI yet, so CORS stays closed for this endpoint.
 *
 * <p>Optional {@code target} attaches the image in the same request:
     * {@code avatar} (profile photo), {@code thumbnail} (project card,
     * requires {@code project}), {@code project-image} (case-study figure,
     * requires {@code project}, accepts {@code alt}/{@code caption}), or
     * {@code problem-visual-image} (one case visual, requires project, problem
     * kind/order, and visual order).
 */
@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
public class AdminImageController {

    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDto<ImageResponseDto>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String target,
            @RequestParam(required = false) String project,
            @RequestParam(required = false) String alt,
            @RequestParam(required = false) String caption,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String problemKind,
            @RequestParam(required = false) Integer problemOrder,
            @RequestParam(required = false) Integer visualOrder) {
        ImageResponseDto response = imageService.upload(
                file,
                target,
                project,
                alt,
                caption,
                title,
                problemKind,
                problemOrder,
                visualOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.ok(response));
    }
}
