package com.gameet.notification.service;

import com.gameet.common.util.Map2JsonSerializer;
import com.gameet.notification.dto.TemplatedEmailRequest;
import com.gameet.notification.enums.AwsSesTemplateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsSesEmailNotifier {

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    private final SesV2Client sesV2Client;
    private final Map2JsonSerializer jsonSerializer;

    public void sendTemplatedEmail(TemplatedEmailRequest templatedEmailRequest) {
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                    .destination(getDestination(templatedEmailRequest.toEmail()))
                    .content(getEmailContent(templatedEmailRequest.awsSesTemplateType(), templatedEmailRequest.templateData()))
                    .fromEmailAddress(fromEmailAddress)
                    .build();

            sesV2Client.sendEmail(emailRequest);
        } catch (SesV2Exception e) {
            log.error("[sendTemplatedEmail] 이메일 전송 실패. toEmail={}, 오류={}", templatedEmailRequest.toEmail(), e.getMessage(), e);
        }
    }

    private Destination getDestination(String toEmail) {
        return Destination.builder()
                .toAddresses(toEmail)
                .build();
    }

    private EmailContent getEmailContent(AwsSesTemplateType templateType, Map<String, String> templateData) {
        return EmailContent.builder()
                .template(getTemplate(templateType, templateData))
                .build();
    }

    private Template getTemplate(AwsSesTemplateType templateType, Map<String, String> templateData) {
        return Template.builder()
                    .templateName(templateType.getTemplateName())
                    .templateData(jsonSerializer.serializeAsString(templateData))
                    .build();
    }
}
