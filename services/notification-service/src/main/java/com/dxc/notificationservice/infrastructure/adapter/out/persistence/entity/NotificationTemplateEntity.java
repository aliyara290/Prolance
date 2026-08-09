package com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "notification_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String type;

    @Column(name = "title_template", nullable = false, length = 500)
    private String titleTemplate;

    @Column(name = "message_template", nullable = false, columnDefinition = "TEXT")
    private String messageTemplate;

    @Column(name = "email_subject_template", length = 500)
    private String emailSubjectTemplate;

    @Column(name = "email_template_name", length = 100)
    private String emailTemplateName;

    @Column(length = 100)
    private String icon;

    @Column(name = "default_action_url_pattern", length = 500)
    private String defaultActionUrlPattern;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(name = "default_priority", nullable = false, length = 20)
    private String defaultPriority;
}
