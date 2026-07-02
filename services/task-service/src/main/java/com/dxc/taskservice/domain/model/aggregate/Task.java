package com.dxc.taskservice.domain.model.aggregate;

import com.dxc.taskservice.domain.exception.BusinessRuleException;
import com.dxc.taskservice.domain.exception.StateTransitionException;
import com.dxc.taskservice.domain.exception.ValidationException;
import com.dxc.taskservice.domain.model.entity.*;
import com.dxc.taskservice.domain.model.event.assignment.AssignmentUpdated;
import com.dxc.taskservice.domain.model.event.assignment.UserAssigned;
import com.dxc.taskservice.domain.model.event.assignment.UserUnassigned;
import com.dxc.taskservice.domain.model.event.attachment.AttachmentAdded;
import com.dxc.taskservice.domain.model.event.attachment.AttachmentRemoved;
import com.dxc.taskservice.domain.model.event.comment.CommentAdded;
import com.dxc.taskservice.domain.model.event.comment.CommentEdited;
import com.dxc.taskservice.domain.model.event.comment.CommentRemoved;
import com.dxc.taskservice.domain.model.event.dependency.DependencyAdded;
import com.dxc.taskservice.domain.model.event.dependency.DependencyRemoved;
import com.dxc.taskservice.domain.model.event.task.TaskCreated;
import com.dxc.taskservice.domain.model.event.task.TaskDeleted;
import com.dxc.taskservice.domain.model.event.task.TaskStatusChanged;
import com.dxc.taskservice.domain.model.event.task.TaskUpdated;
import com.dxc.taskservice.domain.model.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Task extends AggregateRoot {

    private final UUID id;
    private final UUID tenantId;
    private final UUID projectId;
    private UUID milestoneId;

    private String title;
    private String description;

    private TaskType type;
    private TaskPriority priority;
    private TaskStatus status;
    private TaskTimeline timeline;

    private final UUID createdBy;
    private UUID reporterId;

    private final List<TaskAssignment> assignments;
    private final List<TaskComment> comments;
    private final List<TaskAttachment> attachments;
    private final List<TaskStatusHistory> statusHistory;
    private final List<TaskDependency> dependencies;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Factory
    public static Task create(
            UUID tenantId,
            UUID projectId,
            UUID milestoneId,
            String title,
            String description,
            TaskType type,
            TaskStatus status,
            TaskPriority priority,
            LocalDateTime startDate,
            LocalDateTime dueDate,
            UUID createdBy,
            UUID reporterId
    ) {
        validateInitialData(tenantId, projectId, title, type, priority, startDate, createdBy);

        TaskTimeline taskTimeline = TaskTimeline.of(startDate, dueDate);

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .projectId(projectId)
                .milestoneId(milestoneId)
                .title(title)
                .description(description)
                .type(type)
                .priority(priority)
                .status(status != null ? status : TaskStatus.TODO)
                .timeline(taskTimeline)
                .createdBy(createdBy)
                .reporterId(reporterId != null ? reporterId : createdBy)
                .assignments(new ArrayList<>())
                .comments(new ArrayList<>())
                .attachments(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .dependencies(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        task.statusHistory.add(TaskStatusHistory.create(
                tenantId, task.getId(), null, status, createdBy, "Task created"));

        task.registerEvent(TaskCreated.now(
                tenantId, task.getId(), projectId, title, type, priority, status, createdBy));

        return task;
    }

    // Task mutations
    public void updateTask(
            String title,
            String description,
            TaskType type,
            TaskPriority priority,
            LocalDateTime startDate,
            LocalDateTime dueDate,
            UUID milestoneId,
            UUID actionBy
    ) {
        Map<String, Object> changes = new HashMap<>();
        changes.put("oldTitle", this.title);
        changes.put("newTitle", title);
        changes.put("oldDescription", this.description);
        changes.put("newDescription", description);

        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (description != null) {
            this.description = description;
        }
        if (type != null) {
            this.type = type;
        }
        if (priority != null) {
            this.priority = priority;
        }
        if (milestoneId != null) {
            this.milestoneId = milestoneId;
        }

        if (startDate != null || dueDate != null) {
            this.timeline = this.timeline.updateDates(startDate, dueDate);
        }

        touch();
        registerEvent(TaskUpdated.now(tenantId, id, changes, actionBy));
    }

    public void deleteTask(UUID actionBy) {
        touch();
        registerEvent(TaskDeleted.now(tenantId, id, actionBy));
    }

    // Status workflow
    public void changeStatus(TaskStatus newStatus, UUID actionBy, String comment) {
        validateStatusTransition(this.status, newStatus);

        TaskStatus oldStatus = this.status;
        this.status = newStatus;

        statusHistory.add(TaskStatusHistory.create(
                tenantId, id, oldStatus, newStatus, actionBy, comment));

        if (newStatus == TaskStatus.DONE) {
            this.timeline = this.timeline.complete(LocalDateTime.now());
        }

        touch();
        registerEvent(TaskStatusChanged.now(tenantId, id, oldStatus, newStatus, actionBy));
    }

    // Assignments
    public void assignUser(UUID userId, RoleInTask role, float allocationPercentage, UUID actionBy) {
        boolean alreadyAssigned = assignments.stream()
                .anyMatch(a -> a.getUserId().equals(userId)
                        && a.getRole() == role
                        && a.getStatus() == TaskAssignmentStatus.ACTIVE);

        if (alreadyAssigned) {
            throw new BusinessRuleException("User is already assigned with this role");
        }

        TaskAssignment assignment = TaskAssignment.create(tenantId, id, userId, role, allocationPercentage);
        assignments.add(assignment);

        touch();
        registerEvent(UserAssigned.now(tenantId, id, userId, role, allocationPercentage, actionBy));
    }

    public void unassignUser(UUID userId, UUID actionBy) {
        TaskAssignment assignment = assignments.stream()
                .filter(a -> a.getUserId().equals(userId) && a.getStatus() == TaskAssignmentStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Active assignment not found for this user"));

        assignment.unassign();

        touch();
        registerEvent(UserUnassigned.now(tenantId, id, userId, actionBy));
    }

    public void updateAssignment(UUID userId, RoleInTask newRole, Float newAllocation, UUID actionBy) {
        TaskAssignment assignment = assignments.stream()
                .filter(a -> a.getUserId().equals(userId) && a.getStatus() == TaskAssignmentStatus.ACTIVE)
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Active assignment not found for this user"));

        RoleInTask oldRole = assignment.getRole();

        if (newRole != null && newRole != assignment.getRole()) {
            assignment.updateRole(newRole);
        }
        if (newAllocation != null && newAllocation != assignment.getAllocationPercentage()) {
            assignment.updateAllocation(newAllocation);
        }

        touch();
        registerEvent(AssignmentUpdated.now(tenantId, id, userId, oldRole, newRole != null ? newRole : oldRole, actionBy));
    }

    // Comments
    public void addComment(UUID userId, String content, UUID actionBy) {
        TaskComment comment = TaskComment.create(tenantId, id, userId, content);
        comments.add(comment);

        touch();
        registerEvent(CommentAdded.now(tenantId, id, comment.getId(), userId, actionBy));
    }

    public void editComment(UUID commentId, String newContent, UUID editorUserId, UUID actionBy) {
        TaskComment comment = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Comment not found"));

        comment.editContent(newContent, editorUserId);

        touch();
        registerEvent(CommentEdited.now(tenantId, id, commentId, actionBy));
    }

    public void removeComment(UUID commentId, UUID actionBy) {
        TaskComment comment = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Comment not found"));

        comments.remove(comment);

        touch();
        registerEvent(CommentRemoved.now(tenantId, id, commentId, actionBy));
    }

    // Attachments
    public void addAttachment(String fileUrl, String fileName, String fileType, UUID uploadedBy, UUID actionBy) {
        TaskAttachment attachment = TaskAttachment.create(tenantId, id, fileUrl, fileName, fileType, uploadedBy);
        attachments.add(attachment);

        touch();
        registerEvent(AttachmentAdded.now(tenantId, id, attachment.getId(), fileName, actionBy));
    }

    public void removeAttachment(UUID attachmentId, UUID actionBy) {
        TaskAttachment attachment = attachments.stream()
                .filter(a -> a.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Attachment not found"));

        attachments.remove(attachment);

        touch();
        registerEvent(AttachmentRemoved.now(tenantId, id, attachmentId, actionBy));
    }

    // Dependencies
    public void addDependency(UUID dependOnTaskId, DependencyType type, UUID actionBy) {
        boolean alreadyExists = dependencies.stream()
                .anyMatch(d -> d.getDependOnTaskId().equals(dependOnTaskId) && d.getType() == type);

        if (alreadyExists) {
            throw new BusinessRuleException("This dependency already exists");
        }

        TaskDependency dependency = TaskDependency.create(tenantId, id, dependOnTaskId, type);
        dependencies.add(dependency);

        touch();
        registerEvent(DependencyAdded.now(tenantId, id, dependOnTaskId, type, actionBy));
    }

    public void removeDependency(UUID dependencyId, UUID actionBy) {
        TaskDependency dependency = dependencies.stream()
                .filter(d -> d.getId().equals(dependencyId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Dependency not found"));

        dependencies.remove(dependency);

        touch();
        registerEvent(DependencyRemoved.now(tenantId, id, dependency.getDependOnTaskId(), actionBy));
    }

    // Validation
    private void validateStatusTransition(TaskStatus current, TaskStatus next) {
        if (current == next) return;

        boolean valid = switch (current) {
            case TODO -> next == TaskStatus.IN_PROGRESS || next == TaskStatus.CANCELLED;
            case IN_PROGRESS -> next == TaskStatus.IN_REVIEW || next == TaskStatus.TODO || next == TaskStatus.CANCELLED;
            case IN_REVIEW -> next == TaskStatus.DONE || next == TaskStatus.IN_PROGRESS || next == TaskStatus.CANCELLED;
            case DONE, CANCELLED -> false;
        };

        if (!valid) {
            throw new StateTransitionException("Invalid status transition from " + current + " to " + next);
        }
    }

    private static void validateInitialData(
            UUID tenantId, UUID projectId, String title,
            TaskType type, TaskPriority priority, LocalDateTime startDate, UUID createdBy
    ) {
        require(tenantId, "Tenant ID is required");
        require(projectId, "Project ID is required");
        require(title, "Task title is required");
        require(type, "Task type is required");
        require(priority, "Task priority is required");
        require(startDate, "Start date is required");
        require(createdBy, "Creator ID is required");

        if (title.isBlank()) {
            throw new ValidationException("Task title cannot be blank");
        }
    }

    private static void require(Object o, String message) {
        if (o == null) throw new ValidationException(message);
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
