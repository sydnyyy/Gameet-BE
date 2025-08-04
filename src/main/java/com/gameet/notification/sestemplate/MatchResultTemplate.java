package com.gameet.notification.sestemplate;

import com.gameet.notification.enums.AwsSesTemplateType;
import org.springframework.stereotype.Component;

@Component
public class MatchResultTemplate implements EmailTemplateProvider {

    @Override
    public AwsSesTemplateType getTemplateType() {
        return AwsSesTemplateType.MATCH_RESULT;
    }

    @Override
    public String getSubject() {
        return "[Gameet] 매칭 결과 알림입니다.";
    }

    @Override
    public String getHtmlBody() {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                    <h2>매칭 결과 알림</h2>
                    <p>{{matchResult}}</p>
                    <p>페이지에 접속해주세요!</p>
                </body>
                </html>
                """;
    }

    @Override
    public String getTextBody() {
        return """
                매칭 결과 알림
                
                {{matchResult}}
                페이지에 접속해주세요!""";
    }
}
