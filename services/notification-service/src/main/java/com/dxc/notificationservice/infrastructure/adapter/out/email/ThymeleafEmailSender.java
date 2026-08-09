package com.dxc.notificationservice.infrastructure.adapter.out.email;

import com.dxc.notificationservice.application.port.out.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThymeleafEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateModel) {
        try {
            Context context = new Context();
            context.setVariables(templateModel);
            String htmlContent = templateEngine.process("email/" + templateName, context);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            ClassPathResource logoResource = new ClassPathResource("templates/assets/img/logo.png");
            if (logoResource.exists()) {
                helper.addInline("prolance-logo", logoResource, "image/png");
            }

            javaMailSender.send(message);
            log.debug("Email sent to {} with subject: {}", to, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to {}", to, e);
        }
    }
}
