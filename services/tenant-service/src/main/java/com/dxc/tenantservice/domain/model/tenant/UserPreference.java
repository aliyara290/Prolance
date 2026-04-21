package com.dxc.tenantservice.domain.model.tenant;

import com.dxc.tenantservice.domain.model.enums.Language;
import com.dxc.tenantservice.domain.model.enums.Theme;
import lombok.Getter;
import java.util.UUID;

@Getter
public class UserPreference {

    private final UUID id;
    private final UUID tenantId;
    private final UUID userId;

    private Language language;
    private String timezone;
    private Theme theme;

    private UserPreference(
            UUID id,
            UUID tenantId,
            UUID userId,
            Language language,
            String timezone,
            Theme theme
    ) {
        validate(tenantId, userId);

        this.id = id;
        this.tenantId = tenantId;
        this.userId = userId;
        this.language = language;
        this.timezone = timezone;
        this.theme = theme;
    }

    public static UserPreference createDefault(
            UUID tenantId,
            UUID userId,
            TenantSettings tenantSettings
    ) {
        return new UserPreference(
                UUID.randomUUID(),
                tenantId,
                userId,
                tenantSettings.getLanguage(),
                tenantSettings.getTimezone(),
                Theme.LIGHT
        );
    }
    private void validate(UUID tenantId, UUID userId) {
        if (tenantId == null) throw new IllegalArgumentException("tenantId is required");
        if (userId == null) throw new IllegalArgumentException("userId is required");
    }

    public void changeLanguage(Language language) {
        if (language == null) throw new IllegalArgumentException("language cannot be null");
        this.language = language;
    }

    public void changeTheme(Theme theme) {
        if (theme == null) throw new IllegalArgumentException("theme cannot be null");
        this.theme = theme;
    }

    public void changeTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            throw new IllegalArgumentException("timezone invalid");
        }
        this.timezone = timezone;
    }
}