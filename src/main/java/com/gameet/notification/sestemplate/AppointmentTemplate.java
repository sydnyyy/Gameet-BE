package com.gameet.notification.sestemplate;

import com.gameet.notification.enums.AwsSesTemplateType;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTemplate implements EmailTemplateProvider {

    @Override
    public AwsSesTemplateType getTemplateType() {
        return AwsSesTemplateType.APPOINTMENT;
    }

    @Override
    public String getSubject() {
        return "[Gameet] {{time}} 예약 시간이 다가옵니다!";
    }

    @Override
    public String getHtmlBody() {
        return """
                <!DOCTYPE html>
                <html>
                <body>
                    <h2>예약 시간 알림</h2>
                    <p><strong>{{time}}</strong> 예약 시간이 다가옵니다!</p>
                    <p>게임에 접속해주세요.</p>
                </body>
                </html>
                """;
    }

    @Override
    public String getTextBody() {
        return """
                예약 시간 알림
                
                {{time}} 예약 시간이 다가옵니다!
                게임에 접속해주세요.""";
    }
}