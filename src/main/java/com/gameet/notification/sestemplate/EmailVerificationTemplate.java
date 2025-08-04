package com.gameet.notification.sestemplate;

import com.gameet.notification.enums.AwsSesTemplateType;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationTemplate implements EmailTemplateProvider {

    @Override
    public AwsSesTemplateType getTemplateType() {
        return AwsSesTemplateType.EMAIL_VERIFICATION;
    }

    @Override
    public String getSubject() {
        return "[Gameet] {{emailPurpose}} 이메일 인증 코드입니다.";
    }

    @Override
    public String getHtmlBody() {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                    <h2>이메일 인증</h2>
                    <p>인증 코드는 <strong>{{verificationCode}}</strong> 입니다.</p>
                    <p>인증 코드 입력 기한은 5분입니다.</p>
                    <p>코드를 공유하지 마세요.</p>
                </body>
                </html>
                """;
    }

    @Override
    public String getTextBody() {
        return """
                이메일 인증
                
                인증 코드는 {{verificationCode}} 입니다.
                인증 코드 입력 기한은 5분입니다.
                코드를 공유하지 마세요.""";
    }
}
