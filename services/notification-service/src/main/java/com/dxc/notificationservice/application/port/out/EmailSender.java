package com.dxc.notificationservice.application.port.out;

import java.util.Map;

public interface EmailSender {
    void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel);
}
