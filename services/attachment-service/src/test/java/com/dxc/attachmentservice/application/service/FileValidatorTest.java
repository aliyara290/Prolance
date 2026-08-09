package com.dxc.attachmentservice.application.service;

import com.dxc.attachmentservice.domain.exception.InvalidAttachmentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileValidatorTest {

    private final FileValidator validator = new FileValidator();

    @Test
    @DisplayName("Should accept valid PDF file")
    void validate_shouldAcceptValidPdf() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", "application/pdf",
                new byte[1024]);

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept valid JPEG image")
    void validate_shouldAcceptValidJpeg() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg",
                new byte[2048]);

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept valid PNG image")
    void validate_shouldAcceptValidPng() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "screenshot.png", "image/png",
                new byte[512]);

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should accept valid plain text file")
    void validate_shouldAcceptPlainText() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "notes.txt", "text/plain",
                "some text".getBytes());

        assertThatCode(() -> validator.validate(file)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should reject empty file")
    void validate_shouldRejectEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "empty.pdf", "application/pdf",
                new byte[0]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    @DisplayName("Should reject null file")
    void validate_shouldRejectNullFile() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidAttachmentException.class);
    }

    @Test
    @DisplayName("Should reject oversized file")
    void validate_shouldRejectOversizedFile() {
        byte[] oversizedContent = new byte[26 * 1024 * 1024]; // 26 MB
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.pdf", "application/pdf",
                oversizedContent);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("25 MB");
    }

    @Test
    @DisplayName("Should reject disallowed content type")
    void validate_shouldRejectDisallowedContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "app.zip", "application/zip",
                new byte[100]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("not allowed");
    }

    @Test
    @DisplayName("Should reject executable file .exe")
    void validate_shouldRejectExeFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "malware.exe", "application/pdf",
                new byte[100]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("Executable");
    }

    @Test
    @DisplayName("Should reject executable file .sh")
    void validate_shouldRejectShellScript() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "script.sh", "text/plain",
                new byte[100]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("Executable");
    }

    @Test
    @DisplayName("Should reject executable file .bat")
    void validate_shouldRejectBatFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "run.bat", "text/plain",
                new byte[100]);

        assertThatThrownBy(() -> validator.validate(file))
                .isInstanceOf(InvalidAttachmentException.class)
                .hasMessageContaining("Executable");
    }

    @Test
    @DisplayName("Should validate content type string directly")
    void validateContentType_shouldRejectInvalidType() {
        assertThatThrownBy(() -> validator.validateContentType("application/octet-stream"))
                .isInstanceOf(InvalidAttachmentException.class);
    }

    @Test
    @DisplayName("Should accept valid content type string")
    void validateContentType_shouldAcceptValidType() {
        assertThatCode(() -> validator.validateContentType("image/png")).doesNotThrowAnyException();
    }
}
