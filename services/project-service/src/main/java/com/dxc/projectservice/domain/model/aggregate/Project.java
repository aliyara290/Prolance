package com.dxc.projectservice.domain.model.aggregate;

import com.dxc.projectservice.domain.exception.BusinessRuleException;
import com.dxc.projectservice.domain.exception.StateTransitionException;
import com.dxc.projectservice.domain.exception.ValidationException;
import com.dxc.projectservice.domain.model.entity.*;
import com.dxc.projectservice.domain.model.event.member.MemberAdded;
import com.dxc.projectservice.domain.model.event.member.MemberRemoved;
import com.dxc.projectservice.domain.model.event.member.MemberRoleUpdated;
import com.dxc.projectservice.domain.model.event.milestone.MilestoneAdded;
import com.dxc.projectservice.domain.model.event.milestone.MilestoneCompleted;
import com.dxc.projectservice.domain.model.event.milestone.MilestoneDeleted;
import com.dxc.projectservice.domain.model.event.milestone.MilestoneUpdated;
import com.dxc.projectservice.domain.model.event.project.ProjectCreated;
import com.dxc.projectservice.domain.model.event.project.ProjectDeleted;
import com.dxc.projectservice.domain.model.event.project.ProjectStatusChanged;
import com.dxc.projectservice.domain.model.event.project.ProjectUpdated;
import com.dxc.projectservice.domain.model.event.resource.ResourceAdded;
import com.dxc.projectservice.domain.model.event.resource.ResourceRemoved;
import com.dxc.projectservice.domain.model.valueobject.*;
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
public class Project extends AggregateRoot {
    private final UUID id;
    private final UUID tenantId;
    private final UUID clientId;
    private final UUID ownerId;
    private final UUID opportunityId;
    private String name;
    private String description;

    private String prefix;

    private ProjectStatus status;
    private ProjectPriority priority;
    private ProjectTimeline timeline;
    private ProjectFinancials financials;
    private ProjectProgress progress;
    private UUID projectManagerId;

    private final List<Member> members;
    private final List<Milestone> milestones;
    private final List<ProjectResource> resources;
    private final List<ProjectStatusHistory> statusHistory;
    private ProjectMetrics metrics;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final UUID createdBy;

    public static Project create(
            UUID tenantId,
            UUID clientId,
            UUID opportunityId,
            String name,
            String description,
            ProjectPriority priority,
            ProjectTimeline timeline,
            ProjectFinancials financials,
            UUID projectManagerId,
            UUID createdBy
    ) {
        validateInitialData(tenantId, clientId, name, priority, projectManagerId, createdBy);

        Project project = Project.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .clientId(clientId)
                .ownerId(createdBy)
                .opportunityId(opportunityId)
                .name(name)
                .prefix(generatePrefix(name))
                .description(description)
                .status(ProjectStatus.PLANNED)
                .priority(priority)
                .timeline(timeline)
                .financials(financials != null ? financials : ProjectFinancials.empty())
                .progress(ProjectProgress.zero())
                .projectManagerId(projectManagerId)
                .members(new ArrayList<>())
                .milestones(new ArrayList<>())
                .resources(new ArrayList<>())
                .statusHistory(new ArrayList<>())
                .metrics(ProjectMetrics.initial(tenantId))
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();

        project.statusHistory.add(ProjectStatusHistory.create(
                tenantId, null, ProjectStatus.PLANNED, createdBy, "Project initialized"));


        project.registerEvent(ProjectCreated.now(
                tenantId, project.getId(), clientId, name, ProjectStatus.PLANNED, priority, projectManagerId, createdBy));

        return project;
    }

    public void addMember(UUID userId, MemberRole role, int allocation, UUID actionBy) {
        if (members.stream().anyMatch(m -> m.getUserId().equals(userId))) {
            throw new BusinessRuleException("User is already a member of this project");
        }

        validateTotalAllocation(allocation);

        members.add(Member.create(tenantId, id, userId, role, allocation, actionBy));
        touch();
        registerEvent(MemberAdded.now(tenantId, id, userId, role, allocation, actionBy));
    }

    public void removeMember(UUID userId, UUID actionBy) {
        Member member = members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Member not found"));

        member.remove();

        touch();
        registerEvent(MemberRemoved.now(tenantId, id, userId, actionBy));
    }


    public void updateMemberRole(UUID userId, MemberRole newRole, UUID actionBy) {
        Member member = members.stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Member not found"));

        MemberRole oldRole = member.getRole();
        member.updateRole(newRole);

        touch();
        registerEvent(MemberRoleUpdated.now(tenantId, id, userId, oldRole, newRole, actionBy));
    }

    public void addMilestone(String title, String description, LocalDateTime start, LocalDateTime due, int order, UUID actionBy) {
        milestones.add(Milestone.create(tenantId, title, description, start, due, order, actionBy));
        touch();
        registerEvent(MilestoneAdded.now(tenantId, id, title, actionBy));
    }

    public void completeMilestone(UUID milestoneId, UUID actionBy) {
        Milestone milestone = milestones.stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Milestone not found"));

        milestone.complete();
        recalculateProgress();
        touch();
        registerEvent(MilestoneCompleted.now(tenantId, id, milestoneId, actionBy));
    }

    public void updateProject(String name, String description, UUID actionBy) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("oldName", this.name);
        payload.put("newName", name);
        payload.put("oldDescription", this.description);
        payload.put("newDescription", description);

        this.name = name;
        this.description = description;
        this.prefix = generatePrefix(this.name);
        touch();
        registerEvent(ProjectUpdated.now(tenantId, id, payload, actionBy));
    }

    public void deleteProject(UUID actionBy) {
        touch();
        registerEvent(ProjectDeleted.now(tenantId, id, actionBy));
    }

    public void updateMilestone(UUID milestoneId, String title, String description, LocalDateTime start, LocalDateTime due, UUID actionBy) {
        Milestone milestone = milestones.stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Milestone not found"));

        Map<String, Object> payload = new HashMap<>();
        payload.put("oldTitle", milestone.getTitle());
        payload.put("newTitle", title);
        payload.put("oldDescription", milestone.getDescription());
        payload.put("newDescription", description);
        payload.put("oldStart", milestone.getStartDate());
        payload.put("newStart", start);
        payload.put("oldDue", milestone.getDueDate());
        payload.put("newDue", due);

        milestone.update(title, description, start, due);
        touch();
        registerEvent(MilestoneUpdated.now(tenantId, id, milestoneId, payload, actionBy));
    }

    public void deleteMilestone(UUID milestoneId, UUID actionBy) {
        Milestone milestone = milestones.stream()
                .filter(m -> m.getId().equals(milestoneId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Milestone not found"));

        milestones.remove(milestone);
        recalculateProgress();
        touch();
        registerEvent(MilestoneDeleted.now(tenantId, id, milestoneId, actionBy));
    }

    public void changeStatus(ProjectStatus newStatus, UUID actionBy, String comment) {
        validateStatusTransition(this.status, newStatus);

        ProjectStatus oldStatus = this.status;
        this.status = newStatus;
        statusHistory.add(ProjectStatusHistory.create(tenantId, oldStatus, newStatus, actionBy, comment));

        if (newStatus == ProjectStatus.ACTIVE && timeline.actualStartDate() == null) {
            this.timeline = timeline.start(LocalDateTime.now());
        } else if (newStatus == ProjectStatus.COMPLETED) {
            this.timeline = timeline.finish(LocalDateTime.now());
            this.progress = ProjectProgress.complete();
        }

        touch();
        registerEvent(ProjectStatusChanged.now(tenantId, id, oldStatus, newStatus, actionBy));
    }

    public void addResource(String name, String description, ResourceType type, String url, UUID actionBy) {
        ProjectResource resource = ProjectResource.builder()
                .id(UUID.randomUUID())
                .tenantId(this.tenantId)
                .projectId(this.id)
                .name(name)
                .description(description)
                .type(type)
                .url(url)
                .createdAt(LocalDateTime.now())
                .createdBy(actionBy)
                .build();

        this.resources.add(resource);
        touch();
        registerEvent(ResourceAdded.now(tenantId, id, resource.getId(), name, actionBy));
    }

    public void removeResource(UUID resourceId, UUID actionBy) {
        ProjectResource resource = this.resources.stream()
                .filter(r -> r.getId().equals(resourceId))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleException("Resource not found"));

        this.resources.remove(resource);
        touch();
        registerEvent(ResourceRemoved.now(tenantId, id, resourceId, actionBy));
    }

    private void recalculateProgress() {
        if (milestones.isEmpty()) return;

        float totalProgress = (float) milestones.stream()
                .mapToDouble(Milestone::getProgressPercentage)
                .average()
                .orElse(0);

        this.progress = new ProjectProgress(totalProgress);
    }

    private void validateTotalAllocation(int newAllocation) {
        int currentTotal = members.stream()
                .filter(m -> m.getStatus() == MemberStatus.ACTIVE)
                .mapToInt(Member::getAllocationPercentage)
                .sum();

        if (currentTotal + newAllocation > 500) {
            throw new BusinessRuleException("Total project allocation cannot exceed 500%");
        }
    }

    private static void validateInitialData(UUID tenantId, UUID clientId, String name, ProjectPriority priority, UUID managerId, UUID creatorId) {
        require(tenantId, "Tenant ID is required");
        require(clientId, "Client ID is required");
        require(name, "Project name is required");
        require(priority, "Project priority is required");
        require(managerId, "Project manager ID is required");
        require(creatorId, "Creator ID is required");
    }

    private void validateStatusTransition(ProjectStatus current, ProjectStatus next) {
        if (current == next) return;

        boolean valid = switch (current) {
            case PLANNED, ON_HOLD -> next == ProjectStatus.ACTIVE || next == ProjectStatus.CANCELLED;
            case ACTIVE ->
                    next == ProjectStatus.ON_HOLD || next == ProjectStatus.COMPLETED || next == ProjectStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };

        if (!valid) {
            throw new StateTransitionException("Invalid status transition from " + current + " to " + next);
        }
    }

    private static void require(Object o, String message) {
        if (o == null) throw new ValidationException(message);
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static String generatePrefix(String name) {
        if (name == null || name.isBlank()) {
            require(name, "Project name is required");
        }

        return java.util.Arrays.stream(name.trim().split("[\\s_-]+"))
                .filter(word -> !word.isBlank())
                .map(word -> String.valueOf(Character.toUpperCase(word.charAt(0))))
                .reduce("", String::concat);
    }
}