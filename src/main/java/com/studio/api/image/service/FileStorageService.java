package com.studio.api.image.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Writes uploaded images to {@code app.upload.dir}. Files are stored under a
 * random UUID name — the client-supplied filename never touches the
 * filesystem, which rules out path traversal. Both the extension and the
 * declared content type must be on the allowlist.
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/jpg");

    private final Path root;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.root = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create upload directory: " + root, e);
        }
    }

    public Path getRoot() {
        return root;
    }

    /** Validates and stores the file; returns the generated stored name. */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String extension = extensionOf(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Unsupported file extension: " + extension);
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new IllegalArgumentException("Unsupported content type: " + contentType);
        }
        verifyFileSignature(file, extension);
        String storedName = UUID.randomUUID() + "." + extension;
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, root.resolve(storedName));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store uploaded file", e);
        }
        return storedName;
    }

    /**
     * Best-effort cleanup used when the surrounding database transaction rolls
     * back after the file has already been written.
     */
    public void deleteQuietly(String storedName) {
        Path target = root.resolve(storedName).normalize();
        if (!target.getParent().equals(root)) {
            log.warn("Refusing to delete a file outside the upload directory: {}", target);
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.error("Failed to clean up uploaded file after transaction rollback: {}", target, e);
        }
    }

    private void verifyFileSignature(MultipartFile file, String extension) {
        byte[] signature;
        try (InputStream input = file.getInputStream()) {
            signature = input.readNBytes(12);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to inspect uploaded file", e);
        }

        boolean valid = switch (extension) {
            case "jpg", "jpeg" -> startsWith(signature, 0xFF, 0xD8, 0xFF);
            case "png" -> startsWith(signature, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
            case "webp" -> startsWith(signature, 0x52, 0x49, 0x46, 0x46)
                    && signature.length >= 12
                    && Arrays.equals(
                            Arrays.copyOfRange(signature, 8, 12),
                            new byte[] {'W', 'E', 'B', 'P'});
            default -> false;
        };
        if (!valid) {
            throw new IllegalArgumentException("File content does not match its extension");
        }
    }

    private boolean startsWith(byte[] actual, int... expected) {
        if (actual.length < expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (Byte.toUnsignedInt(actual[i]) != expected[i]) {
                return false;
            }
        }
        return true;
    }

    private String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        if (dot < 0 || dot == filename.length() - 1) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
