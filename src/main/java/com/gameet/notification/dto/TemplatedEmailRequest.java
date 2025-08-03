package com.gameet.notification.dto;

import com.gameet.notification.enums.AwsSesTemplateType;
import lombok.Builder;

import java.util.Map;

@Builder
public record TemplatedEmailRequest (

        String toEmail,
        AwsSesTemplateType awsSesTemplateType,
        Map<String, String> templateData
) {
}
