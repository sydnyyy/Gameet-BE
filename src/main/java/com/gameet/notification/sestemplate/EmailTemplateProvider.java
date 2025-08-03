package com.gameet.notification.sestemplate;

import com.gameet.notification.enums.AwsSesTemplateType;

public interface EmailTemplateProvider {

    AwsSesTemplateType getTemplateType();
    String getSubject();
    String getHtmlBody();
    String getTextBody();
}
