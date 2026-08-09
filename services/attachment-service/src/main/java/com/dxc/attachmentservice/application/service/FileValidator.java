package com.dxc.attachmentservice.application.service;

import com.dxc.attachmentservice.domain.exception.InvalidAttachmentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@Slf4j
public class FileValidator {

    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25 MB

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
    );

    private static final Set<String> EXECUTABLE_EXTENSIONS = Set.of(
            ".exe", ".bat", ".cmd", ".sh", ".ps1", ".msi",
            ".com", ".vbs", ".js", ".jar", ".py", ".rb",
            ".bin", ".cgi", ".pl", ".app", ".action", ".command"
    );

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidAttachmentException("File is empty or missing");
        }

        validateSize(file);
        validateContentType(file);
        validateNotExecutable(file);
    }

    public void validateContentType(String contentType) {
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            log.warn("Rejected content type: {}", contentType);
            throw new InvalidAttachmentException(
                    "Content type '" + contentType + "' is not allowed. Allowed types: " + ALLOWED_CONTENT_TYPES);
        }
    }

    private void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            log.warn("File size {} exceeds maximum allowed size of {} bytes", file.getSize(), MAX_FILE_SIZE);
            throw new InvalidAttachmentException(
                    "File size exceeds maximum allowed size of 25 MB. Actual size: "
                            + (file.getSize() / (1024 * 1024)) + " MB");
        }
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        validateContentType(contentType);
    }

    private void validateNotExecutable(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new InvalidAttachmentException("File name is missing");
        }

        String lowerName = originalFilename.toLowerCase();
        for (String ext : EXECUTABLE_EXTENSIONS) {
            if (lowerName.endsWith(ext)) {
                log.warn("Rejected executable file: {}", originalFilename);
                throw new InvalidAttachmentException("Executable files are not allowed: " + originalFilename);
            }
        }
    }
}
