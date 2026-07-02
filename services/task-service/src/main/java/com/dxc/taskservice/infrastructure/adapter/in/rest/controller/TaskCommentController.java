package com.dxc.taskservice.infrastructure.adapter.in.rest.controller;

import com.dxc.taskservice.application.dto.comment.req.AddCommentRequest;
import com.dxc.taskservice.application.dto.comment.req.EditCommentRequest;
import com.dxc.taskservice.application.dto.comment.res.TaskCommentResponse;
import com.dxc.taskservice.application.port.in.TaskCommentUseCase;
import com.dxc.taskservice.infrastructure.adapter.in.rest.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
public class TaskCommentController {

    private final TaskCommentUseCase commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<TaskCommentResponse>> addComment(
            @PathVariable("taskId") UUID taskId,
            @Valid @RequestBody AddCommentRequest request) {
        TaskCommentResponse response = commentService.addComment(taskId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<TaskCommentResponse>> editComment(
            @PathVariable("taskId") UUID taskId,
            @PathVariable("commentId") UUID commentId,
            @Valid @RequestBody EditCommentRequest request) {
        TaskCommentResponse response = commentService.editComment(taskId, commentId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> removeComment(
            @PathVariable("taskId") UUID taskId,
            @PathVariable("commentId") UUID commentId) {
        commentService.removeComment(taskId, commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskCommentResponse>>> getComments(
            @PathVariable("taskId") UUID taskId) {
        List<TaskCommentResponse> response = commentService.getComments(taskId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
