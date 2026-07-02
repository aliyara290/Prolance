package com.dxc.taskservice.application.dto.comment.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditCommentRequest(
    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Comment cannot exceed 2000 characters")
    String content
) {}
