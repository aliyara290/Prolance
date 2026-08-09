package com.dxc.notificationservice.bootstrap;

import com.dxc.notificationservice.domain.model.valueobject.NotificationCategory;
import com.dxc.notificationservice.domain.model.valueobject.NotificationPriority;
import com.dxc.notificationservice.domain.model.valueobject.NotificationType;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationTemplateEntity;
import com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa.NotificationTemplateRepositoryJpa;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateSeeder implements CommandLineRunner {

    private final NotificationTemplateRepositoryJpa repository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding notification templates...");

        seedTemplate(
                "PROJECT_MANAGER_ASSIGNED",
                "Assigned as Project Manager: ${projectName}",
                "You have been assigned as the project manager for project: ${projectName} by ${creatorName}.",
                "You've Been Assigned as Project Manager",
                "project-manager-assigned",
                "user",
                "/app/projects/all/${projectId}",
                "PROJECT",
                "HIGH"
        );

        seedTemplate(
                NotificationType.MEMBER_INVITED.name(),
                "You have been invited to Prolance",
                "You have been invited to join the company ${companyName} by ${inviterName}.",
                "Invitation to Prolance",
                "member-invitation",
                "envelope",
                "/invitations",
                NotificationCategory.SYSTEM.name(),
                NotificationPriority.HIGH.name()
        );

        seedTemplate(
                NotificationType.TASK_ASSIGNED.name(),
                "New Task Assigned: ${taskTitle}",
                "You have been assigned to a new task: ${taskTitle} in project ${projectName}.",
                "New Task Assignment - Prolance",
                "task-assigned",
                "clipboard-list",
                "/app/projects/all/${projectId}/tasks?taskId=${taskId}",
                NotificationCategory.TASK.name(),
                NotificationPriority.HIGH.name()
        );

        seedTemplate(
                NotificationType.MEMBER_ADDED_TO_PROJECT.name(),
                "Added to Project: ${projectName}",
                "You have been added to the project ${projectName} with the role ${role}.",
                "Project Assignment - Prolance",
                "member-invitation",
                "folder",
                "/app/projects/all/${projectId}",
                NotificationCategory.PROJECT.name(),
                NotificationPriority.MEDIUM.name()
        );

        seedTemplate(
                NotificationType.TASK_STATUS_CHANGED.name(),
                "Task Status Updated: ${taskTitle}",
                "The status of task ${taskTitle} was changed to ${newStatus}.",
                "Task Status Update",
                null,
                "check-circle",
                "/app/projects/all/${projectId}/tasks?taskId=${taskId}",
                NotificationCategory.TASK.name(),
                NotificationPriority.LOW.name()
        );

        seedTemplate(
                NotificationType.PROJECT_CREATED.name(),
                "New Project Created: ${projectName}",
                "A new project ${projectName} was created by ${creatorName}.",
                "New Project",
                null,
                "folder-plus",
                "/app/projects/all/${projectId}",
                NotificationCategory.PROJECT.name(),
                NotificationPriority.MEDIUM.name()
        );

        seedTemplate(
                NotificationType.PROJECT_STATUS_CHANGED.name(),
                "Project Status Updated: ${projectName}",
                "The project ${projectName} status is now ${newStatus}.",
                "Project Status Update",
                null,
                "folder",
                "/app/projects/all/${projectId}",
                NotificationCategory.PROJECT.name(),
                NotificationPriority.MEDIUM.name()
        );

        seedTemplate(
                NotificationType.MILESTONE_COMPLETED.name(),
                "Milestone Completed: ${milestoneName}",
                "The milestone ${milestoneName} in project ${projectName} was completed.",
                "Milestone Completed",
                null,
                "flag",
                "/app/projects/all/${projectId}/milestones/${milestoneId}",
                NotificationCategory.PROJECT.name(),
                NotificationPriority.MEDIUM.name()
        );

        seedTemplate(
                NotificationType.COMMENT_ADDED.name(),
                "New Comment on Task: ${taskTitle}",
                "${commenterName} commented on task ${taskTitle}.",
                "New Comment",
                "comment-added",
                "message-square",
                "/app/projects/all/${projectId}/tasks?taskId=${taskId}#comments",
                NotificationCategory.TASK.name(),
                NotificationPriority.LOW.name()
        );

        log.info("Notification templates seeding completed.");
    }

    private void seedTemplate(String type, String titleTemplate, String messageTemplate,
                              String emailSubjectTemplate, String emailTemplateName,
                              String icon, String defaultActionUrlPattern,
                              String category, String defaultPriority) {
        Optional<NotificationTemplateEntity> existingOpt = repository.findByType(type);
        if (existingOpt.isEmpty()) {
            NotificationTemplateEntity entity = NotificationTemplateEntity.builder()
                    .id(UUID.randomUUID())
                    .type(type)
                    .titleTemplate(titleTemplate)
                    .messageTemplate(messageTemplate)
                    .emailSubjectTemplate(emailSubjectTemplate)
                    .emailTemplateName(emailTemplateName)
                    .icon(icon)
                    .defaultActionUrlPattern(defaultActionUrlPattern)
                    .category(category)
                    .defaultPriority(defaultPriority)
                    .build();
            repository.save(entity);
            log.info("Seeded template for type: {}", type);
        } else {
            NotificationTemplateEntity entity = existingOpt.get();
            entity.setTitleTemplate(titleTemplate);
            entity.setMessageTemplate(messageTemplate);
            entity.setEmailSubjectTemplate(emailSubjectTemplate);
            entity.setEmailTemplateName(emailTemplateName);
            entity.setIcon(icon);
            entity.setDefaultActionUrlPattern(defaultActionUrlPattern);
            entity.setCategory(category);
            entity.setDefaultPriority(defaultPriority);
            repository.save(entity);
            log.info("Updated template for type: {}", type);
        }
    }
}
