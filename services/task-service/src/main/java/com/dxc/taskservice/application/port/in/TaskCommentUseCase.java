package com.dxc.taskservice.application.port.in;

import com.dxc.taskservice.application.dto.comment.req.AddCommentRequest;
import com.dxc.taskservice.application.dto.comment.req.EditCommentRequest;
import com.dxc.taskservice.application.dto.comment.res.TaskCommentResponse;

import java.util.List;
import java.util.UUID;

public interface TaskCommentUseCase {
    TaskCommentResponse addComment(UUID taskId, AddCommentRequest request);
    TaskCommentResponse editComment(UUID taskId, UUID commentId, EditCommentRequest request);
    void removeComment(UUID taskId, UUID commentId);
    List<TaskCommentResponse> getComments(UUID taskId);
}
