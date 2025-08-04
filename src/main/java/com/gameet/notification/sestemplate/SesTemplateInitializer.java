package com.gameet.notification.sestemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.*;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SesTemplateInitializer implements CommandLineRunner {

    private final SesV2Client sesV2Client;
    private final List<EmailTemplateProvider> emailTemplateProviders;

    @Override
    public void run(String... args) {
        emailTemplateProviders.forEach(this::createTemplateIfNotExist);
    }

    private void createTemplateIfNotExist(EmailTemplateProvider provider) {
        try {
            sesV2Client.getEmailTemplate(GetEmailTemplateRequest.builder()
                    .templateName(provider.getTemplateType().getTemplateName())
                    .build());
        } catch (NotFoundException e) {
            try {
                EmailTemplateContent templateContent = EmailTemplateContent.builder()
                        .subject(provider.getSubject())
                        .html(provider.getHtmlBody())
                        .text(provider.getTextBody())
                        .build();

                CreateEmailTemplateRequest createTemplateRequest = CreateEmailTemplateRequest.builder()
                        .templateName(provider.getTemplateType().getTemplateName())
                        .templateContent(templateContent)
                        .build();

                sesV2Client.createEmailTemplate(createTemplateRequest);
                log.info("[AWS SES Template Initializer] {} template 생성 완료", provider.getTemplateType());
           } catch (SesV2Exception ex) {
               log.error("[AWS SES Template Initializer] {} template 생성 중 오류 발생", provider.getTemplateType(), ex);
               // TODO: 디스코드 알림
           }
        }
    }
}